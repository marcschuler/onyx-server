package de.marcschuler.onyxserver.service.gameintegration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SteamID64Test {

    @Test
    void isSteamID64_acceptsValidSteamId() {
        assertTrue(SteamID64.isSteamID64("76561197960287930"));
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
        assertFalse(SteamID64.isSteamID64(id));
    }

    @Test
    void isSteamID64_rejectsNullAndBlank() {
        assertFalse(SteamID64.isSteamID64((String) null));
        assertFalse(SteamID64.isSteamID64("   "));
    }

    @Test
    void fromLong_acceptsValidSteamId() {
        var id = SteamID64.fromLong(76561197960287930L);

        assertEquals(76561197960287930L, id.value());
    }

    @Test
    void fromLong_rejectsInvalidSteamId() {
        assertThrows(IllegalArgumentException.class, () -> SteamID64.fromLong(1234L));
    }
}
