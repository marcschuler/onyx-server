package de.marcschuler.onyxserver.dto;

import de.marcschuler.onyxserver.Util;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeTest {

    private static Challenge challenge() {
        return
                new Challenge(Instant.parse("2026-01-01T00:00:00Z"), "nonce");
    }

    @Test
    void serializesAsObject() {
        var authChallenge = new AuthChallenge(challenge());

        var json = Util.objectMapper.readTree(Util.objectMapper.writeValueAsBytes(authChallenge));

        var challengeJson = json.get("challenge");
        assertTrue(challengeJson.isObject());
        assertEquals("nonce", challengeJson.get("nonce").asString());
    }

    @Test
    void roundTripsAuthChallenge() {
        var original = new AuthChallenge(challenge());

        var decoded = Util.objectMapper.readValue(
                Util.objectMapper.writeValueAsBytes(original), AuthChallenge.class);

        assertEquals(original, decoded);
    }
}
