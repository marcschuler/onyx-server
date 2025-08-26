package de.marcschuler.webrtcserver.service.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.ServerInfoService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import de.marcschuler.webrtcserver.webclient.events.ServerInfoEventBody;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeRequest;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.Vector;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientConnectionService extends TextWebSocketHandler {

    private final ServerInfoService serverInfoService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthService authService;

    private final List<WebClient> sessions = new Vector<>();

    private final List<Class<? extends EventBody>> allowedEventsWhenUnauthorized = List.of(AuthChallengeResponse.class);


    @Override
    public synchronized void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("Added websocket {} from {}", session.getId(), session.getRemoteAddress());
        var client = new WebClient(session);
        client.setState(WebClientState.NOT_AUTHORIZED);
        sessions.add(client);
        sendToClient(client, new AuthChallengeRequest(authService.createChallenge()));
    }

    @Override
    public synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message {} from {}", new String(message.asBytes()), session);
        var event = new ObjectMapper().readValue(message.asBytes(), EventBody.class);
        var client = clientFromSession(session).get();

        if (!client.getState().isInteractionAllowed()) {
            if (!allowedEventsWhenUnauthorized.contains(event.getClass())) {
                log.warn("Client tried to use event without being logged in {}", event.getClass());
                kickClient(client, KickReason.UNAUTHORIZED_REQUEST);
            }
        }

        log.info("Sending event {} to bus", event.getType());
        applicationEventPublisher.publishEvent(new ClientEvent<>(event, client));
    }

    public void moveClient(WebClient client, Channel channel) {
        client.setChannel(channel);
        sendUpdateServerTree();
    }

    public void sendUpdateServerTree() {
        var serverInfo = new ServerInfoEventBody(serverInfoService.getServerInfov0(), sessions);
        serverInfo.setType(EventBody.EventType.SERVER_INFO_TREE);
        for (WebClient session : this.sessions) {
            try {
                sendToClient(session, serverInfo);
            } catch (IOException e) {
                log.error("Could not send to client {}", session, e);
            }
        }
    }

    /**
     * Kicks a client
     *
     * @param client the client to kick
     * @param reason the reason. May be null
     * @throws IOException if there is an error kicking the client.
     */
    public void kickClient(WebClient client, KickReason reason) throws IOException {
        log.info("Kicking client {} for reason: {}", client, reason);
        sessions.remove(client);
        client.getSession().close();
    }

    public void sendToClient(WebClient client, EventBody eventBody) throws IOException {
        var data = new ObjectMapper().writeValueAsBytes(eventBody);
        client.getSession().sendMessage(new TextMessage(data));
    }

    public void sendToAllClients(EventBody eventBody){
        sendToClients(sessions,eventBody);
    }

    public void sendToClients(List<WebClient> clients, EventBody eventBody) {
        var exceptions = false;
        for(var client: clients){
            try {
                sendToClient(client, eventBody);
            }catch (IOException e) {
                e.printStackTrace();
                exceptions = true;
            }
        }
        if (exceptions)
            throw new IllegalStateException("Could not send to at least one client ");
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session for ws:{} closed because {}", session.getId(), status);
        sessions.remove(clientFromSession(session).get());
        sendUpdateServerTree();
    }


    public Optional<WebClient> clientFromSessionId(@NonNull String id) {
        return this.sessions.stream()
                .filter(s -> s.getSession().getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromSession(@NonNull WebSocketSession session) {
        return this.sessions.stream()
                .filter(s -> s.getSession() == session)
                .findFirst();
    }

}
