package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.ClientMessage;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelDetailRequest;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelDetailResponse;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelChangeRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerEventHandler {

    private final WebSocketConnectionService webSocketConnectionService;
    private final ChannelService channelService;

    private final ServerMapper serverMapper;

    @EventListener
    @Transactional
    public void onChannelChangeRequest(ClientMessage<ClientChannelChangeRequest> event) {
        log.info("User {} wants to change channel to {}", event.getClient().getUser().getUsername(), event.getBody().getChannelId());
        var channel = channelService.get(event.getBody().getChannelId()).get();
        webSocketConnectionService.moveClient(event.getClient(), channel);
    }

    @EventListener
    @Transactional
    public void onChannelDetailRequest(ClientMessage<ChannelDetailRequest> event) throws IOException {
        var channel = channelService.get(event.getBody().getChannelId()).get();

        var response = new ChannelDetailResponse(serverMapper.mapToDTO(channel));
        response.setRespondsTo(event.getBody().getRequestId());
        webSocketConnectionService.sendToClient(event.getClient(),
                response);
    }
}
