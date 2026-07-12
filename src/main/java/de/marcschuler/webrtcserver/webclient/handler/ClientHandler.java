package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.PermissionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.ClientMessage;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelJoinRequest;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelLeaveRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientHandler {

    private final WebSocketConnectionService webSocketConnectionService;
    private final ChannelService channelService;
    private final PermissionService permissionService;

    @EventListener
    @Transactional
    public void onChannelJoinRequest(ClientMessage<ClientChannelJoinRequest> event) {
        log.info("User {} wants to join channel {}", event.client().getUser().getUsername(),event.body().channelId());
        var wantedChannel = channelService.get(event.body().channelId()).orElseThrow();
        
        permissionService.checkClientAccess(event.client(), wantedChannel, PermissionType.CHANNEL_JOIN);

        webSocketConnectionService.joinChannel(event.client(), wantedChannel);
    }

    @EventListener
    @Transactional
    public void onChannelLeaveRequest(ClientMessage<ClientChannelLeaveRequest> event) {
        log.info("User {} wants to leave the channel", event.client().getUser().getUsername());
        webSocketConnectionService.leaveChannel(event.client());
    }

}
