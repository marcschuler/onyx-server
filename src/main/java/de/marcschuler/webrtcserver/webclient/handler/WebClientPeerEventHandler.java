package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
import de.marcschuler.webrtcserver.webclient.error.ClientSecurityViolationException;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.peer.PeerAnswer;
import de.marcschuler.webrtcserver.webclient.events.peer.PeerAnswerForward;
import de.marcschuler.webrtcserver.webclient.events.peer.PeerOffer;
import de.marcschuler.webrtcserver.webclient.events.peer.PeerOfferForward;
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
    public void onOffer(ClientEvent<PeerOffer> event) throws IOException {
        var clientFrom = event.getClient().getUser().getUsername();
        log.info("Forwarding peer offer from {} to {}", clientFrom, event.getBody().getClientTo());
        var clientTo = webClientConnectionService.clientFromKeyId(event.getBody().getClientTo()).get();
        var forwardEvent = new PeerOfferForward(event.getClient().getUser().getId(), event.getBody().getOffer());
        if (!event.getClient().getUser().getId().equals(clientTo.getUser().getId())) {
            log.warn("Clients try to connect to each other without being in the same channel");
        }
        webClientConnectionService.sendToClient(clientTo, forwardEvent);
    }

    @EventListener
    public void onAnswer(ClientEvent<PeerAnswer> event) throws IOException {
        var clientFrom = event.getClient().getUser().getUsername();
        log.info("Forwarding peer answer from {} to {}", clientFrom, event.getBody().getClientTo());
        var clientTo = webClientConnectionService.clientFromKeyId(event.getBody().getClientTo()).get();
        var forwardEvent = new PeerAnswerForward(event.getClient().getUser().getId(), event.getBody().getAnswer());
        if (!event.getClient().getUser().getId().equals(clientTo.getUser().getId())) {
            log.warn("Clients try to connect to each other without being in the same channel");
        }
        webClientConnectionService.sendToClient(clientTo, forwardEvent);
    }
}
