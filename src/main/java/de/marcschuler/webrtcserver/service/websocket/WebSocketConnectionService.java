package de.marcschuler.webrtcserver.service.websocket;

import de.marcschuler.webrtcserver.data.Permission;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import de.marcschuler.webrtcserver.service.PolicyService;
import de.marcschuler.webrtcserver.service.policy.PolicyCheckerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import tools.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.error.webclient.NoClientException;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.ClientMessage;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeRequest;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelJoinMessage;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelLeaveMessage;
import de.marcschuler.webrtcserver.webclient.messages.connection.KickMessage;
import jakarta.validation.constraints.NotNull;
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
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionService extends TextWebSocketHandler {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthService authService;
    private final PolicyService policyService;

    @Autowired
    @Lazy
    private WebSocketService webSocketService;

    private final ObjectMapper objectMapper;

    private final ServerMapper serverMapper;

    private final List<WebClient> sessions = new Vector<>();

    private final List<Class<? extends MessageBody>> allowedEventsWhenUnauthorized = List.of(AuthChallengeResponse.class);


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
        log.debug("Received message {} from {}", new String(message.asBytes()), session);
        var event = objectMapper.readValue(message.asBytes(), MessageBody.class);
        var client = clientFromSession(session).orElseThrow(() -> new NoClientException("Client for session " + session.getId() + " not found"));

        if (!client.getState().isInteractionAllowed()) {
            if (!allowedEventsWhenUnauthorized.contains(event.getClass())) {
                log.warn("Client tried to use event without being logged in {}", event.getClass());
                kickClient(client, KickReason.UNAUTHORIZED_REQUEST);
            }
        }

        log.debug("Sending event {} to bus", event.getClass().getSimpleName());
        try {
            applicationEventPublisher.publishEvent(new ClientMessage<>(event, client));
        } catch (PolicyCheckException e) {
            log.info("User did not have permission to {}: {}", e.getMessage(), e.getMessage());
            log.debug("Exception was", e);
        } catch (Exception e) {
            log.error("Uncaught exception while handling a message from client '{}'", client, e);
            throw new RuntimeException(e);
        }
    }

    public void moveClient(WebClient client, Channel channel) throws PolicyCheckException {
        policyService.checkAccess(channel.getPolicies().get(Permission.PermissionType.CHANNEL_JOIN),
                new PolicyCheckerContext(Permission.PermissionType.CHANNEL_JOIN, client.getUser(),
                        channel, Map.of()));

        var channelBefore = client.getChannel();
        client.setChannel(channel);

        if (channel != null) {
            sendToAllClients(new ClientChannelJoinMessage(
                    serverMapper.mapToDTO(client.getUser()),
                    channel.getId()
            ));
        } else if (channelBefore != null) {
            sendToAllClients(new ClientChannelLeaveMessage(serverMapper.mapToDTO(client.getUser())));
        } else {
            log.warn("Ignored channel change request because moving from null to null");
        }
        webSocketService.updateServerTree();
    }


    /**
     * Kicks a client
     *
     * @param client the client to kick
     * @param reason the reason. May be null
     * @throws IOException if there is an error kicking the client.
     */
    public void kickClient(WebClient client, KickReason reason) throws IOException {
        client.setState(WebClientState.INVALID);
        log.info("Kicking client {} for reason: {}", client, reason);
        try {
            sendToClient(client, new KickMessage(reason));
        } catch (Exception e) { //we can ignore this
            log.warn("Kicking message failed", e);
        }
        sessions.remove(client);
        client.getSession().close();
    }

    public void sendToClient(WebClient client, MessageBody messageBody) throws IOException {
        var data = objectMapper.writeValueAsBytes(messageBody);
        log.debug("Sending to client {}: {}", client, data);
        client.getSession().sendMessage(new TextMessage(data));
    }

    public void sendToAllClients(MessageBody messageBody) {
        sendToClients(sessions, messageBody);
    }

    public void sendToClients(List<WebClient> clients, MessageBody messageBody) {
        var exceptions = false;
        for (var client : clients) {
            try {
                sendToClient(client, messageBody);
            } catch (IOException e) {
                log.error("Could not send to a single client", e);
                exceptions = true;
            }
        }
        if (exceptions)
            throw new IllegalStateException("Could not send to at least one client ");
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session for ws:{} closed because {}", session.getId(), status);
        clientFromSession(session)
                .ifPresent(client -> {
                    sessions.remove(client);
                    moveClient(client, null); //TODO may send another event type?
                });
    }


    public Optional<WebClient> clientFromSessionId(@NonNull String id) {
        return this.sessions.stream()
                .filter(s -> s.getSession().getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromKeyId(@NotNull String id) {
        // User might be null if a client is connected for the first time but hasn't authenticated
        return this.sessions.stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromSession(@NonNull WebSocketSession session) {
        return this.sessions.stream()
                .filter(s -> s.getSession() == session)
                .findFirst();
    }

    @Deprecated
    public List<WebClient> clients() {
        return this.sessions;
    }

    /**
     * All clients that are interactable. Basically logged in and have a user field.
     *
     * @return
     */
    public List<WebClient> clientsInteractable() {
        return this.sessions.stream()
                .filter(c -> c.getState().isInteractionAllowed())
                .toList();
    }

    public List<User> users() {
        return clientsInteractable().stream()
                .map(WebClient::getUser)
                .filter(Objects::nonNull)
                .toList();
    }
}
