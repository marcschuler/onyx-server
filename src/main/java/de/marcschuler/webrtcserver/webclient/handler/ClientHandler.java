package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChannelService;
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

    @EventListener
    @Transactional
    public void onChannelJoinRequest(ClientMessage<ClientChannelJoinRequest> event) {
        log.info("User {} wants to  join channel {}", event.getClient().getUser().getUsername(), event.getBody().getChannelId());
        var currentChannel = event.getClient().getChannel();
        var wantedChannel = channelService.get(event.getBody().getChannelId()).orElseThrow();
        if (currentChannel == wantedChannel){
            log.info("Client wanted to join already used channel");
            return;
        }
        webSocketConnectionService.joinChannel(event.getClient(), wantedChannel);
    }

    @EventListener
    @Transactional
    public void onChannelLeaveRequest(ClientMessage<ClientChannelLeaveRequest> event) {
        log.info("User {} wants leave his channel", event.getClient().getUser().getUsername());
        webSocketConnectionService.leaveChannel(event.getClient());
    }

}
