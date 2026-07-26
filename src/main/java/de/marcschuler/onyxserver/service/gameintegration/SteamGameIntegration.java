package de.marcschuler.onyxserver.service.gameintegration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SteamGameIntegration implements GameIntegration<String> {

    private RestClient restClient;

    private final Map<Integer, String> gameNames = new HashMap<>();

    @Value("${onyx.integration.steam.key}")
    private String steamKey;

    @Override
    public Optional<ClientCurrentGame> getCurrentlyPlaying(String id) {
        return Optional.empty();
    }

    @Override
    public void updateGameList(List<String> ids) {
        //TODO
    }


    @Override
    public int getOrder() {
        return 0;
    }

    private SteamPlayerSummariesResponse steamGetPlayerSummariesv0002(List<String> ids) {
        var idArray = ids.stream().collect(Collectors.joining(","));
        return this.restClient.get().uri(u -> u.path("ISteamUser/GetPlayerSummaries/v2/")
                        .queryParam("key", steamKey)
                        .queryParam("steamids", idArray).build())
                .retrieve()
                .body(SteamPlayerSummariesResponse.class);
    }

    public boolean isSteamID64(String id) {
        if (id == null || id.isBlank())
            return false;
        if (id.length() != 17)
            return false;
        if (!id.startsWith("7656119"))
            return false;
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SteamPlayerSummariesResponse(
            Response response
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Response(
                List<Player> players
        ) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Player(
                String steamid,
                int communityvisibilitystate,
                int profilestate,
                String personaname,
                long lastlogoff,
                String profileurl,
                String avatar,
                String avatarmedium,
                String avatarfull
        ) {
        }
    }
}
