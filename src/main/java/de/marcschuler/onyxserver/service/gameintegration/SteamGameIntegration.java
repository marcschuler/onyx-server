package de.marcschuler.onyxserver.service.gameintegration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.marcschuler.onyxserver.service.websocket.WebSocketService;
import de.marcschuler.onyxserver.webclient.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "onyx.game-integration.steam.api-key")
@Slf4j
public class SteamGameIntegration implements GameIntegration<String> {

    private final WebSocketService webSocketService;
    private final SteamWebClient steamWebClient;


    private final LoadingCache<Long, Game> gameCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(7, TimeUnit.DAYS)
            .build(new CacheLoader<>() {
                @Override
                public Game load(@NonNull Long appId) throws Exception {
                    return getGameData(appId);
                }
            });

    @Override
    public void updateClients(List<WebClient> clients) {
        var ids = clients.stream()
                .map(client -> client.getIntegration().getSteamId64())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        var games = updateGameList(ids);
        log.debug("Game list is {}", games);
        clients.forEach(client -> {
            var steamId = client.getIntegration().getSteamId64();
            var game = Optional.ofNullable(steamId)
                    .map(games::get)
                    .orElse(null);
            client.setGame(game);
        });
    }

    /**
     *
     * @param ids a list of steamid64
     * @return a Map of SteamID64 to the currently played game
     */
    public Map<SteamID64, Game> updateGameList(List<SteamID64> ids) {
        var response = this.steamWebClient.steamGetPlayerSummariesv0002(ids);
        return response.response().players().stream()
                .filter(p -> p.gameId() != null)
                .map(p -> {
                    try {
                        var game = gameCache.get(p.gameId());
                        return Map.entry(SteamID64.fromLong(Long.parseLong(p.steamid())), game);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public int getOrder() {
        return 0;
    }

    public Game getGameData(long appId) throws IOException {
        var gameData = steamWebClient.steamAppDetails(appId);
        var icon = steamWebClient.downloadGameIcon(steamWebClient.getSteamIconUrl(appId, gameData.imageIconUrl()));

        return new Game(String.valueOf(appId), gameData.name(), "", icon);
    }

}
