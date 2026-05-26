package de.marcschuler.webrtcserver.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.webclient.messages.auth.JwtTokenEvent;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @Value("${onyx.auth.challenge.expiration}")
    private Duration challengeExpiration;

    @Value("${onyx.auth.jwt.expiration}")
    private Duration jwtExpiration;
    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    void init() {
        //Remove old challenges
        executorService.scheduleAtFixedRate(() -> {
            synchronized (challenges) {
                this.challenges.removeIf(challenge -> challenge.getValidUntil().isAfter(Instant.now()));
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Scheduled(fixedRateString = "${onyx.auth.jwt.refresh}", initialDelayString = "1m")
    public void refreshToken() {
        if (webSocketConnectionService.clientsInteractable().isEmpty())
            return;
        log.debug("Reissuing JWT for all clients with a lifetime of {}", jwtExpiration);
        webSocketConnectionService.clientsInteractable()
                .forEach(client -> {
                    try {
                        @SuppressWarnings("DataFlowIssue") var jwt = createJWT(client.getUser());
                        client.sendMessage(new JwtTokenEvent(jwt));
                    } catch (IOException | JOSEException e) {
                        log.error("Could not send message to client", e);
                    }
                });
    }

    public AuthChallenge createChallenge() {
        var challenge = new AuthChallenge(cryptoService.generateChallenge(), Instant.now().plus(challengeExpiration.toMillis(), ChronoUnit.MILLIS));
        synchronized (challenges) {
            challenges.add(challenge);
        }
        return challenge;
    }

    /**
     * Checks that the challenge is a valid challenge from the server and
     * it is answered in time. A challenge may be checked multiple times
     * using this function
     *
     * @param challenge the challenge to check
     * @return true if valid, false if invalid
     */
    public boolean isValidChallenge(@NotNull String challenge) {
        synchronized (challenges) {
            return challenges.stream()
                    .filter(c -> c.getChallenge().equals(challenge))
                    .anyMatch(c -> c.getValidUntil().isAfter(Instant.now()));
        }
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
