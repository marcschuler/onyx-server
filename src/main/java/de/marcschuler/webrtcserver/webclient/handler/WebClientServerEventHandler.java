package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.channel.ChannelDetailRequest;
import de.marcschuler.webrtcserver.webclient.events.channel.ChannelDetailResponse;
import de.marcschuler.webrtcserver.webclient.events.client.ClientChannelChangeRequest;
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

    private final WebClientConnectionService webClientConnectionService;
    private final ServerInfoService serverInfoService;

    private final ServerMapper serverMapper;

    @EventListener
    @Transactional
    public void onChannelChangeRequest(ClientEvent<ClientChannelChangeRequest> event) {
        log.info("User {} wants to change channel to {}", event.getClient().getUser().getUsername(), event.getBody().getChannelId());
        var channel = serverInfoService.channelById(event.getBody().getChannelId()).get();
        webClientConnectionService.moveClient(event.getClient(), channel);
    }

    @EventListener
    @Transactional
    public void onChannelDetailRequest(ClientEvent<ChannelDetailRequest> event) throws IOException {
        var channel = serverInfoService.channelById(event.getBody().getChannelId()).get();

        var response = new ChannelDetailResponse(serverMapper.mapToDTO(channel));
        response.setRespondsTo(event.getBody().getRequestId());
        webClientConnectionService.sendToClient(event.getClient(),
                response);
    }
}
