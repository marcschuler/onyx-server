package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.webclient.events.ChannelChangeRequest;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerEventService {

    private final WebSocketSignalingService webSocketSignalingService;
    private final ServerInfoService serverInfoService;


    @EventListener
    public void onLogin(ClientEvent<LoginEvent> event) {
        log.info("User login as {}", event.getEvent().getUsername());
        event.getClient().setUsername(event.getEvent().getUsername());
        webSocketSignalingService.moveClient(event.getClient(), serverInfoService.getServerInfo().getDefaultChannel());
    }

    @EventListener
    public void onChannelChangeRequest(ClientEvent<ChannelChangeRequest> event) {
        log.info("User {} wants to change channel to {}", event.getClient().getUsername(), event.getEvent().getChannelId());
        var channel = serverInfoService.channelById(event.getEvent().getChannelId()).get();
        webSocketSignalingService.moveClient(event.getClient(), channel);
    }
}
