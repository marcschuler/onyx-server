package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.error.webclient.ProblemDetailException;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.webclient.messages.auth.JwtTokenMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
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

    private final ServerService serverService;
    @Autowired
    @Lazy
    private WebSocketConnectionService webSocketConnectionService;

    private final ScheduledExecutorService executorService;

    private final List<AuthChallenge> challenges = new ArrayList<>();

    @Value("${iris.auth.challenge.expiration}")
    private Duration challengeExpiration;

    @Value("${iris.auth.jwt.expiration}")
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

    @Scheduled(fixedRateString = "${iris.auth.jwt.refresh}", initialDelayString = "1m")
    public void refreshToken() {
        log.debug("Reissuing JWT for all clients with a lifetime of {}", jwtExpiration);
        webSocketConnectionService.clientsInteractable()
                .forEach(client -> {
                    @SuppressWarnings("DataFlowIssue") var jwt = createJWT(client.getUser());
                    try {
                        client.sendMessage(new JwtTokenMessage(jwt));
                    } catch (IOException e) {
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
    public String createJWT(User user) {
        return Jwts.builder()
                .subject(user.getId())
                .issuer(applicationName) //TODO
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration.toMillis()))
                .signWith(serverService.defaultServer().getKeys().getPrivate())
                .compact();
    }

    /**
     * Validates the jwt
     *
     * @param jwt the jwt
     * @return the user id (subject)
     */
    public String verifyJWT(String jwt) {
        var claims = Jwts.parser()
                .verifyWith(serverService.defaultServer().getKeys().getPublic())
                .build()
                .parseSignedClaims(jwt);
        return claims.getBody().getSubject();
    }

}
