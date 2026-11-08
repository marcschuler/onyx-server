package de.marcschuler.onyxserver.service.gameintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.onyxserver.data.file.Hash;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * Test double that serves recorded Steam API responses from the test resources
 * instead of calling the network. Each method can be backed by a scenario
 * directory, e.g. {@code steam/player-summaries/no-game/76561197960287930.json}.
 */
public class SteamWebClientMock extends SteamWebClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String playerSummariesScenario;

    public SteamWebClientMock(String playerSummariesScenario) {
        this.playerSummariesScenario = playerSummariesScenario;
    }

    @Override
    public SteamPlayerSummariesResponse steamGetPlayerSummariesv0002(List<SteamID64> ids) {
        var players = ids.stream()
                .map(SteamID64::toString)
                .map(id -> readFixture(
                        "steam/player-summaries/" + playerSummariesScenario + "/" + id + ".json",
                        SteamPlayerSummariesResponse.class))
                .map(SteamPlayerSummariesResponse::response)
                .map(SteamPlayerSummariesResponse.Response::players)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();

        return new SteamPlayerSummariesResponse(new SteamPlayerSummariesResponse.Response(players));
    }

    @Override
    public SteamGameData steamAppDetails(long appId) throws IOException {
        throw new UnsupportedOperationException("No recorded app details fixture for app " + appId);
    }

    @Override
    public Hash downloadGameIcon(URL iconUrl) throws IOException {
        throw new UnsupportedOperationException("No recorded game icon fixture for " + iconUrl);
    }

    private static <T> T readFixture(String path, Class<T> type) {
        try (var in = SteamWebClientMock.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing Steam fixture: " + path);
            }
            return OBJECT_MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read Steam fixture: " + path, e);
        }
    }
}
