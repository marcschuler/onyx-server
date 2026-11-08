package de.marcschuler.onyxserver.service;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.dto.AuthChallengeProof;
import de.marcschuler.onyxserver.dto.Challenge;
import de.marcschuler.onyxserver.error.webclient.ClientSecurityException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testJWT() throws JOSEException, ParseException {
        var user = new User();
        user.setId("test");
        var jwt = authService.createJWT(user);
        assertNotNull(jwt);

        var userId = authService.verifyJWT(jwt);
        assertEquals("test",userId);
    }

    private AuthChallengeProof proof(Challenge challenge, String origin) {
        return new AuthChallengeProof(AuthChallengeProof.TYPE, challenge, origin);
    }

    @Test
    void validProofIsAcceptedOnce() {
        var challenge = authService.createChallenge().challenge();

        assertDoesNotThrow(() -> authService.validateProof(proof(challenge, "http://localhost:8080")));
        assertThrows(ClientSecurityException.class,
                () -> authService.validateProof(proof(challenge, "http://localhost:8080")), "replay rejected");
    }

    @Test
    void equivalentOriginIsAccepted() {
        var challenge = authService.createChallenge().challenge();

        assertDoesNotThrow(() -> authService.validateProof(proof(challenge, "WS://LocalHost:8080/ws")));
    }

    @Test
    void relayedProofWithForeignOriginIsRejected() {
        var challenge = authService.createChallenge().challenge();

        var e = assertThrows(ClientSecurityException.class,
                () -> authService.validateProof(proof(challenge, "https://evil.example.com")));
        assertTrue(e.getMessage().contains("http://localhost:8080"), "lists allowed origins: " + e.getMessage());
        assertThrows(ClientSecurityException.class, () -> authService.validateProof(proof(challenge, "https://localhost:8080")), "scheme must match");
        assertThrows(ClientSecurityException.class, () -> authService.validateProof(proof(challenge, "not an origin")));
    }

    @Test
    void wrongTypeIsRejected() {
        var challenge = authService.createChallenge().challenge();

        assertThrows(ClientSecurityException.class, () -> authService.validateProof(
                new AuthChallengeProof("something-else", challenge, "http://localhost:8080")));
    }

    @Test
    void unknownChallengeIsRejected() {
        var challenge = new Challenge(Instant.now(), "not-issued-by-server");

        assertThrows(ClientSecurityException.class, () -> authService.validateProof(proof(challenge, "http://localhost:8080")));
    }

    @Test
    void expiredChallengeIsRejected() {
        var issued = authService.createChallenge().challenge();
        // same nonce, but pretend it was issued long ago -> does not match the stored challenge
        var forged = new Challenge(Instant.EPOCH, issued.nonce());

        assertThrows(ClientSecurityException.class, () -> authService.validateProof(proof(forged, "http://localhost:8080")));
    }

    @Test
    void normalizeOrigin() {
        assertEquals("https://example.com", AuthService.normalizeOrigin("wss://Example.com:443/ws?x=1"));
        assertEquals("http://example.com:8080", AuthService.normalizeOrigin("http://example.com:8080/"));
        assertEquals("http://[::1]:8080", AuthService.normalizeOrigin("ws://[::1]:8080"));
        assertThrows(IllegalArgumentException.class, () -> AuthService.normalizeOrigin("ftp://example.com"));
        assertThrows(IllegalArgumentException.class, () -> AuthService.normalizeOrigin("https://user@example.com"));
        assertThrows(IllegalArgumentException.class, () -> AuthService.normalizeOrigin("example.com"));
    }

}
