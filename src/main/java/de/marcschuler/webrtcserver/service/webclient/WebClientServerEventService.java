package de.marcschuler.webrtcserver.service.webclient;

import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.webclient.events.ChannelChangeRequest;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.LoginEventBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerEventService {

    private final WebClientConnectionService webClientConnectionService;
    private final ServerInfoService serverInfoService;


    @EventListener
    public void onLogin(ClientEvent<LoginEventBody> event) {
        log.info("User login as {}", event.getBody().getUsername());
        event.getClient().setUsername(event.getBody().getUsername());
        webClientConnectionService.moveClient(event.getClient(), serverInfoService.getServerInfov0().getDefaultChannel());
    }

    @EventListener
    public void onChannelChangeRequest(ClientEvent<ChannelChangeRequest> event) {
        log.info("User {} wants to change channel to {}", event.getClient().getUsername(), event.getBody().getChannelId());
        var channel = serverInfoService.channelById(event.getBody().getChannelId()).get();
        webClientConnectionService.moveClient(event.getClient(), channel);
    }
}
