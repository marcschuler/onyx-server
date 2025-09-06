package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
import de.marcschuler.webrtcserver.webclient.events.ChannelChangeRequest;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerEventHandler {

    private final WebClientConnectionService webClientConnectionService;
    private final ServerInfoService serverInfoService;

    @EventListener
    public void onChannelChangeRequest(ClientEvent<ChannelChangeRequest> event) {
        log.info("User {} wants to change channel to {}", event.getClient().getUser().getUsername(), event.getBody().getChannelId());
        var channel = serverInfoService.channelById(event.getBody().getChannelId()).get();
        webClientConnectionService.moveClient(event.getClient(), channel);
    }
}
