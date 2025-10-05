package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
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

/**
 * Forwards SDP data.
 * We are only a proxy that allows clients to forward the data and connect
 * TODO more security checks: Can clients connect? (same channel? permissions?)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientPeerEventHandler {

    private final WebSocketConnectionService webSocketConnectionService;

    @EventListener
    public void onOffer(ClientEvent<PeerOffer> event) throws IOException {
        var clientFrom = event.getClient().getUser().getUsername();
        log.info("Forwarding peer offer from {} to {}", clientFrom, event.getBody().getClientTo());
        var clientTo = webSocketConnectionService.clientFromKeyId(event.getBody().getClientTo()).get();
        var forwardEvent = new PeerOfferForward(event.getClient().getUser().getId(), event.getBody().getOffer());


        var clientFromChannel = event.getClient().getChannel();
        var clientToChannel = clientTo.getChannel();
        if (clientToChannel == null || !clientToChannel.equals(clientFromChannel)) {
            log.warn("Clients try to connect to each other without being in the same channel {}<->{}", clientFromChannel, clientToChannel);
        }
        webSocketConnectionService.sendToClient(clientTo, forwardEvent);
    }

    @EventListener
    public void onAnswer(ClientEvent<PeerAnswer> event) throws IOException {
        var clientFrom = event.getClient().getUser().getUsername();
        log.info("Forwarding peer answer from {} to {}", clientFrom, event.getBody().getClientTo());
        var clientTo = webSocketConnectionService.clientFromKeyId(event.getBody().getClientTo()).get();
        var forwardEvent = new PeerAnswerForward(event.getClient().getUser().getId(), event.getBody().getAnswer());

        var clientFromChannel = event.getClient().getChannel();
        var clientToChannel = clientTo.getChannel();
        if (clientToChannel == null || !clientToChannel.equals(clientFromChannel)) {
            log.warn("Clients try to connect to each other without being in the same channel {}<->{}", clientFromChannel, clientToChannel);
        }
        webSocketConnectionService.sendToClient(clientTo, forwardEvent);
    }
}
