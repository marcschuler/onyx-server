package de.marcschuler.onyxserver.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.dto.AuthChallenge;
import de.marcschuler.onyxserver.dto.AuthChallengeProof;
import de.marcschuler.onyxserver.dto.Challenge;
import de.marcschuler.onyxserver.error.webclient.ClientSecurityException;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.auth.JwtTokenEvent;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CryptoService cryptoService;
    @Autowired
    @Lazy
    private ServerService serverService;
    @Autowired
    @Lazy
    private WebSocketConnectionService webSocketConnectionService;

    private final ScheduledExecutorService executorService;

    private final List<AuthChallenge> challenges = new ArrayList<>();

    @Value("${onyx.auth.expire-login}")
    private Duration challengeExpiration;

    @Value("${onyx.auth.jwt.expiration}")
    private Duration jwtExpiration;
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * The origins (scheme, host, port) this server is reachable under. A client signs the origin it connected to,
     * see {@link AuthChallengeProof}
     */
    @Value("${onyx.auth.server}")
    private List<String> configuredOrigins;

    private Set<String> allowedOrigins;

    @PostConstruct
    void init() {
        if (configuredOrigins == null || configuredOrigins.isEmpty())
            throw new IllegalStateException("onyx.auth.server must contain at least one origin");
        allowedOrigins = configuredOrigins.stream()
                .map(AuthService::normalizeOrigin)
                .collect(Collectors.toUnmodifiableSet());
        log.info("Accepting authentication for origins {}", allowedOrigins);

        //Remove expired challenges
        executorService.scheduleAtFixedRate(() -> {
            synchronized (challenges) {
                this.challenges.removeIf(challenge -> isExpired(challenge.challenge()));
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Scheduled(fixedRateString = "${onyx.auth.jwt.refresh}", initialDelayString = "1m")
    public void refreshToken() {
        if (webSocketConnectionService.clients().isEmpty())
            return;
        log.debug("Reissuing JWT for all clients with a lifetime of {}", jwtExpiration);
        webSocketConnectionService.clients()
                .forEach(client -> {
                    try {
                        @SuppressWarnings("DataFlowIssue") var jwt = createJWT(client.getUser());
                        client.send(new JwtTokenEvent(jwt));
                    } catch (JOSEException e) {
                        log.error("Could not send message to client", e);
                    }
                });
    }

    public AuthChallenge createChallenge() {
        var challenge = new AuthChallenge(cryptoService.generateChallenge());
        synchronized (challenges) {
            challenges.add(challenge);
        }
        return challenge;
    }

    /**
     * Validates a proof signed by a client. The signature itself must already be verified.
     * On success the challenge is consumed and can not be used again.
     *
     * @param proof the signed proof
     * @throws ClientSecurityException if the proof is invalid. The message may be shown to the end user
     */
    public void validateProof(@NotNull AuthChallengeProof proof) throws ClientSecurityException {
        if (!AuthChallengeProof.TYPE.equals(proof.type()))
            throw new ClientSecurityException("Invalid authentication type '" + proof.type()
                    + "', expected '" + AuthChallengeProof.TYPE + "'. The client may be outdated");
        if (proof.challenge() == null)
            throw new ClientSecurityException("No challenge given in the authentication proof");
        if (proof.origin() == null)
            throw new ClientSecurityException("No origin given in the authentication proof. The client may be outdated");
        String origin;
        try {
            origin = normalizeOrigin(proof.origin());
        } catch (IllegalArgumentException e) {
            throw new ClientSecurityException("The origin '" + proof.origin()
                    + "' is not a valid address. Expected an address like https://example.com", e);
        }
        if (!allowedOrigins.contains(origin))
            throw new ClientSecurityException("This server does not accept connections via '" + origin
                    + "'. Allowed addresses are: " + allowedOrigins.stream().sorted().collect(Collectors.joining(", ")));
        if (!consumeChallenge(proof.challenge()))
            throw new ClientSecurityException("The authentication challenge is unknown, expired or was already used. Please reconnect");
    }

    /**
     * Checks that the challenge was issued by this server and is answered in time.
     * The challenge is removed, so every challenge can only be used once.
     *
     * @param challenge the challenge to check
     * @return true if valid, false if invalid
     */
    public boolean consumeChallenge(@NotNull Challenge challenge) {
        synchronized (challenges) {
            var it = challenges.iterator();
            while (it.hasNext()) {
                var c = it.next().challenge();
                if (c.equals(challenge)) {
                    it.remove();
                    return !isExpired(c);
                }
            }
            return false;
        }
    }

    private boolean isExpired(Challenge challenge) {
        return challenge.issuedAt().plus(challengeExpiration).isBefore(Instant.now());
    }

    /**
     * Normalizes an origin to {@code scheme://host[:port]}. The scheme and host are lowercased,
     * ws/wss are mapped to http/https, default ports and any path, query or fragment are dropped.
     *
     * @param origin an url, e.g. {@code wss://Example.com:443/ws}
     * @return the normalized origin, e.g. {@code https://example.com}
     * @throws IllegalArgumentException if the origin is not a valid http(s)/ws(s) url
     */
    public static String normalizeOrigin(@NotNull String origin) {
        URI uri;
        try {
            uri = new URI(origin.trim());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid origin " + origin, e);
        }
        if (uri.getScheme() == null || uri.getHost() == null || uri.getRawUserInfo() != null)
            throw new IllegalArgumentException("Invalid origin " + origin);
        var scheme = switch (uri.getScheme().toLowerCase(Locale.ROOT)) {
            case "http", "ws" -> "http";
            case "https", "wss" -> "https";
            default -> throw new IllegalArgumentException("Invalid scheme in origin " + origin);
        };
        var host = uri.getHost().toLowerCase(Locale.ROOT);
        var port = uri.getPort();
        if (port == ("http".equals(scheme) ? 80 : 443))
            port = -1;
        return scheme + "://" + host + (port == -1 ? "" : ":" + port);
    }

    /**
     * Creates an jwt
     *
     * @param user the user
     * @return the jwt as string
     */
    public String createJWT(User user) throws JOSEException {
        var header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID(user.getId())
                .type(JOSEObjectType.JWT)
                .build();
        var claims = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer(applicationName)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtExpiration.toMillis()))
                .build();

        var signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(new Ed25519Signer(serverService.defaultServer().getKeys()));
        return signedJWT.serialize();
    }

    /**
     * Validates the jwt
     *
     * @param jwt the jwt
     * @return the user id (subject)
     */
    public String verifyJWT(String jwt) throws JOSEException, ParseException {
        var parsed = SignedJWT.parse(jwt);
        var valid = parsed.verify(new Ed25519Verifier(serverService.defaultServer().getKeys().toPublicJWK()));
        if (!valid) {
            log.error("No verifiable JWT: {}", jwt);
            throw new SecurityException("Could not verify JWT");
        }

        return parsed.getJWTClaimsSet().getSubject();
    }

}
