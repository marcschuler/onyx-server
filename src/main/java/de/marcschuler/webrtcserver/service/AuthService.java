package de.marcschuler.webrtcserver.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CryptoService cryptoService;

    private final ScheduledExecutorService executorService;

    private final List<AuthChallenge> challenges = new ArrayList<>();

    @Value("${iris.auth.challenge.validMinutes}")
    private int validMinutes;

    @PostConstruct
    void init() {
        //Remove old challenges
        executorService.scheduleAtFixedRate(() -> {
            synchronized (challenges) {
                this.challenges.removeIf(challenge -> challenge.validUntil.isAfter(Instant.now()));
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public AuthChallenge createChallenge() {
        var challenge = new AuthChallenge(cryptoService.generateChallenge(), Instant.now().plus(5, ChronoUnit.MINUTES));
        synchronized (challenges) {
            challenges.add(challenge);
        }
        return challenge;
    }

    /**
     * Checks that the challenge is a valid challenge from the server and
     * it is answered in time. A challenge may be checked multiple times
     * using this function
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


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthChallenge {
        @NotNull
        private String challenge;
        @NotNull
        private Instant validUntil;
    }
}
