package de.marcschuler.onyxserver.webclient.handler;

import de.marcschuler.onyxserver.service.ChannelService;
import de.marcschuler.onyxserver.service.PermissionService;
import de.marcschuler.onyxserver.service.gameintegration.SteamID64;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.messages.gameintegration.GameIntegrationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameIntegrationHandler {


    private final WebSocketConnectionService webSocketConnectionService;
    private final ChannelService channelService;
    private final PermissionService permissionService;

    @EventListener
    @Transactional
    public void onChannelJoinRequest(ClientMessage<GameIntegrationRequest> event) {
        var id = Optional.ofNullable(event.body().steamId64()).map(SteamID64::fromLong)
                        .orElse(null);
        log.info("Client {} set SteamId64 to '{}'", event.client().getUser().getUsername(),id);
        event.client().getIntegration().setSteamId64(id);
    }
}
