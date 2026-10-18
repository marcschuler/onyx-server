package de.marcschuler.onyxserver.webclient.handler;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.WebClient;
import de.marcschuler.onyxserver.webclient.WebClientState;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerAnswer;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerAnswerForward;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerOffer;
import de.marcschuler.onyxserver.webclient.messages.peer.PeerOfferForward;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.node.StringNode;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeerEventHandlerTest {

    @Mock
    private WebSocketConnectionService webSocketConnectionService;

    @InjectMocks
    private PeerEventHandler peerEventHandler;

    private WebClient client(String userId, Channel channel) {
        var user = new User();
        user.setId(userId);
        var client = new WebClient(mock(WebSocketSession.class));
        client.setUser(user);
        client.setState(WebClientState.LOGGED_IN);
        client.setChannel(channel);
        return client;
    }

    @Test
    void onOffer_forwardsOfferToTargetClientInSameChannel() {
        var channel = new Channel();
        channel.setId(UUID.randomUUID());

        var fromClient = client("from-id", channel);
        var toClient = client("to-id", channel);

        var offer = new PeerOffer("to-id", new StringNode("sdp-offer"));
        var message = new ClientMessage<>(offer, fromClient, LocalDateTime.now());

        when(webSocketConnectionService.clientFromKeyId("to-id")).thenReturn(Optional.of(toClient));

        peerEventHandler.onOffer(message);

        var captor = ArgumentCaptor.forClass(PeerOfferForward.class);
        verify(webSocketConnectionService).send(eq(toClient), captor.capture());
        var forwarded = captor.getValue();
        assertEquals("from-id", forwarded.clientFrom());
        assertEquals("sdp-offer", forwarded.offer().asText());
    }

    @Test
    void onAnswer_forwardsAnswerToTargetClient() {
        var channel = new Channel();
        channel.setId(UUID.randomUUID());

        var fromClient = client("from-id", channel);
        var toClient = client("to-id", channel);

        var answer = new PeerAnswer("to-id", new StringNode("sdp-answer"));
        var message = new ClientMessage<>(answer, fromClient, LocalDateTime.now());

        when(webSocketConnectionService.clientFromKeyId("to-id")).thenReturn(Optional.of(toClient));

        peerEventHandler.onAnswer(message);

        var captor = ArgumentCaptor.forClass(PeerAnswerForward.class);
        verify(webSocketConnectionService).send(eq(toClient), captor.capture());
        var forwarded = captor.getValue();
        assertEquals("from-id", forwarded.clientFrom());
        assertEquals("sdp-answer", forwarded.answer().asText());
    }
}
