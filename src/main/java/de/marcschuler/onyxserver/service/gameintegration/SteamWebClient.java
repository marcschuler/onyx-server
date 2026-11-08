package de.marcschuler.onyxserver.service.gameintegration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.marcschuler.onyxserver.data.file.Hash;
import de.marcschuler.onyxserver.service.StorageService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "onyx.game-integration.steam.api-key")
public class SteamWebClient {

    @Autowired
    @Qualifier("integrationStorageService")
    private StorageService storageService;
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.steampowered.com/")
            .build();

    @Value("${onyx.game-integration.steam.api-key}")
    private String steamKey;

    public SteamPlayerSummariesResponse steamGetPlayerSummariesv0002(List<SteamID64> ids) {
        var idArray = ids.stream().map(SteamID64::toString).collect(Collectors.joining(","));
        return this.restClient.get().uri(u -> u.path("ISteamUser/GetPlayerSummaries/v2/")
                        .queryParam("key", steamKey)
                        .queryParam("steamids", idArray).build())
                .retrieve()
                .body(SteamPlayerSummariesResponse.class);
    }

    public SteamGameData steamAppDetails(long appId) throws IOException {
        var response = restClient.get()
                .uri("https://store.steampowered.com/api/appdetails?appids={appId}", appId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, SteamAppDetailsResponse>>() {
                });
        var details = response != null ? response.get(String.valueOf(appId)) : null;
        if (details == null || !details.success() || details.data() == null) {
            throw new IOException("Could not get Steam app details for " + appId);
        }
        return new SteamGameData(details.data().name(), steamIconFileId(appId));
    }

    private String steamIconFileId(long appId) throws IOException {
        var page = restClient.get()
                .uri("https://store.steampowered.com/app/{appId}/", appId)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .body(String.class);
        if (page == null) {
            throw new IOException("Could not load Steam store page for " + appId);
        }
        var matcher = Pattern.compile("community_assets/images/apps/" + appId + "/([a-f0-9]+)\\.jpg").matcher(page);
        if (!matcher.find()) {
            throw new IOException("Could not find Steam icon for app " + appId);
        }
        return matcher.group(1);
    }

    /**
     *
     * @param appId        the app id, e.g. 730
     * @param imageIconUrl the image icon url got by GetOwnedGamesV1 or GetRecentlyPlayedGamesv1. ~40 hexadecimal chars
     * @return the url for the game icon
     * @throws MalformedURLException
     */
    public URL getSteamIconUrl(long appId, String imageIconUrl) throws MalformedURLException {
        return URI.create("https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/apps/" + appId + "/" + imageIconUrl + ".jpg").toURL();
    }

    public Hash downloadGameIcon(URL iconUrl) throws IOException {
        var content = restClient.get()
                .uri(iconUrl.toString())
                .retrieve()
                .body(byte[].class);
        if (content == null) {
            throw new IOException("Could not download game icon from " + iconUrl);
        }
        var hash = new Hash(Hash.HashType.SHA_256, storageService.hash(content));
        storageService.writeFile(hash, content);
        return hash;
    }

    public record SteamGameData(
            String name,
            String imageIconUrl
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SteamAppDetailsResponse(
            boolean success,
            @Nullable AppDetails data
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AppDetails(
            String name
    ) {
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
                String avatarfull,
                @JsonProperty("gameid") @Nullable String gameid,
                @Nullable String gameextrainfo
        ) {
            /**
             * Steam sends the game id as an unsigned 64 bit decimal string. Regular apps fit into 32 bit,
             * non-steam shortcuts and mods have the highest bit set and are therefore negative as signed long.
             *
             * @return the game id as (unsigned) long, or null if the player is not in a game
             */
            @JsonIgnore
            public @Nullable Long gameId() {
                return gameid == null ? null : Long.parseUnsignedLong(gameid);
            }
        }
    }
}
