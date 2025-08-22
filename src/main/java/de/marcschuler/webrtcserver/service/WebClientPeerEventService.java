package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.webclient.error.ClientSecurityViolationException;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.ClientPeerOffer;
import de.marcschuler.webrtcserver.webclient.events.ServerPeerOfferForward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientPeerEventService {

    private final WebSocketSignalingService webSocketSignalingService;
    private final ServerInfoService serverInfoService;

    @EventListener
    public void onOffer(ClientEvent<ClientPeerOffer> event) throws IOException, ClientSecurityViolationException {
        var clientFrom = event.getClient().getUsername();
        log.info("Forwarding peer offer from {} to {}", clientFrom, event.getEvent().getClientTo());
        var clientTo = webSocketSignalingService.clientFromId(event.getEvent().getClientTo()).get();
        var forwardEvent = new ServerPeerOfferForward(event.getEvent().getClientTo(), clientFrom, event.getEvent().getOffer());
        webSocketSignalingService.sendToClient(clientTo, forwardEvent);
    }
}
