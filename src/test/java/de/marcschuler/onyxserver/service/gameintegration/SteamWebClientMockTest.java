package de.marcschuler.onyxserver.service.gameintegration;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamWebClientMockTest {

    private static final long GABEN = 76561197960287930L;
    private static final long NONEXISTENT_PROFILE = 76561199999999999L;

    @Test
    void loadsExistingProfileWithoutRunningGame() {
        var client = new SteamWebClientMock("no-game");

        var response = client.steamGetPlayerSummariesv0002(List.of(SteamID64.fromLong(GABEN)));

        var player = response.response().players().getFirst();
        assertEquals("76561197960287930", player.steamid());
        assertEquals("Rabscuttle", player.personaname());
        assertNull(player.gameId());
        assertNull(player.gameextrainfo());
    }

    @Test
    void loadsRunningGame() {
        var client = new SteamWebClientMock("in-game");

        var player = client.steamGetPlayerSummariesv0002(List.of(SteamID64.fromLong(GABEN)))
                .response().players().getFirst();

        assertEquals(730L, player.gameId());
        assertEquals("Counter-Strike 2", player.gameextrainfo());
    }

    @Test
    void loadsNonSteamShortcutGameIdAsUnsignedLong() {
        var client = new SteamWebClientMock("in-shortcut");

        var player = client.steamGetPlayerSummariesv0002(List.of(SteamID64.fromLong(GABEN)))
                .response().players().getFirst();

        assertEquals("13831578843442315264", Long.toUnsignedString(player.gameId()));
        assertTrue(player.gameId() < 0, "high bit set for shortcuts");
    }

    @Test
    void returnsNoPlayerForNonexistentProfile() {
        var client = new SteamWebClientMock("not-found");

        var response = client.steamGetPlayerSummariesv0002(
                List.of(SteamID64.fromLong(NONEXISTENT_PROFILE)));

        assertTrue(response.response().players().isEmpty());
    }
}
