package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
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
public class WebClientPeerEventHandler {

    private final WebClientConnectionService webClientConnectionService;
    private final ServerInfoService serverInfoService;

    @EventListener
    public void onOffer(ClientEvent<ClientPeerOffer> event) throws IOException, ClientSecurityViolationException {
        var clientFrom = event.getClient().getUser().getUsername();
        log.info("Forwarding peer offer from {} to {}", clientFrom, event.getBody().getClientTo());
        var clientTo = webClientConnectionService.clientFromSessionId(event.getBody().getClientTo()).get();
        var forwardEvent = new ServerPeerOfferForward(event.getBody().getClientTo(), clientFrom, event.getBody().getOffer());
        webClientConnectionService.sendToClient(clientTo, forwardEvent);
    }
}
