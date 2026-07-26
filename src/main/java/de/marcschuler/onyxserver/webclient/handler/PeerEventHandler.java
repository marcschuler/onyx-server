package de.marcschuler.onyxserver.webclient.handler;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerAnswer;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerAnswerForward;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerOffer;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerOfferForward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Forwards SDP data.
 * We are only a proxy that allows clients to forward the data and connect
 * TODO more security checks: Can clients connect? (same channel? permissions?)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PeerEventHandler {

    private final WebSocketConnectionService webSocketConnectionService;

    @EventListener
    public void onOffer(ClientMessage<PeerOffer> event) {
        var clientFrom = event.client().getUser().getUsername();
        log.info("Forwarding peer offer from {} to {}", clientFrom, event.body().clientTo());
        var clientTo = webSocketConnectionService.clientFromKeyId(event.body().clientTo()).get();
        var forwardEvent = new PeerOfferForward(event.client().getUser().getId(), event.body().offer());


        var clientFromChannel = event.client().getChannel();
        var clientToChannel = clientTo.getChannel();
        if (!inSameChannel(clientFromChannel, clientToChannel)) {
            log.warn("Client tried to connect without being in the same channel {}<->{}", clientFromChannel, clientToChannel);
        }
        webSocketConnectionService.send(clientTo, forwardEvent);
    }

    @EventListener
    public void onAnswer(ClientMessage<PeerAnswer> event) {
        var clientFrom = event.client().getUser().getUsername();
        log.info("Forwarding peer answer from {} to {}", clientFrom, event.body().clientTo());
        var clientTo = webSocketConnectionService.clientFromKeyId(event.body().clientTo()).get();
        var forwardEvent = new PeerAnswerForward(event.client().getUser().getId(), event.body().answer());

        var clientFromChannel = event.client().getChannel();
        var clientToChannel = clientTo.getChannel();
        if (!inSameChannel(clientFromChannel, clientToChannel)) {
            log.warn("Client tried to answer without being in the same channel {}<->{}", clientFromChannel, clientToChannel);
        }
        webSocketConnectionService.send(clientTo, forwardEvent);
    }

    private boolean inSameChannel(Channel c1, Channel c2) {
        if (c1 == null || c2 == null) return false;
        return c1.getId().equals(c2.getId());
    }
}
