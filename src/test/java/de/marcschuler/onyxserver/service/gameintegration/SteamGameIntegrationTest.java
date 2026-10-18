package de.marcschuler.onyxserver.service.gameintegration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SteamGameIntegrationTest {

    private final SteamGameIntegration integration = new SteamGameIntegration();

    @Test
    void isSteamID64_acceptsValidSteamId() {
        assertTrue(integration.isSteamID64("76561197960287930"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "not-a-number",
            "12345678901234567",
            "7656119abcdefghij",
            "7656119796028793"
    })
    void isSteamID64_rejectsInvalidIds(String id) {
        assertFalse(integration.isSteamID64(id));
    }

    @Test
    void isSteamID64_rejectsNullAndBlank() {
        assertFalse(integration.isSteamID64(null));
        assertFalse(integration.isSteamID64("   "));
    }
}
