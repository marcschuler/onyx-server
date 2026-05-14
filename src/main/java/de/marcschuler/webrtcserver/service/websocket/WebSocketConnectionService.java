package de.marcschuler.webrtcserver.service.websocket;

import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.error.webclient.ClientKickException;
import de.marcschuler.webrtcserver.error.webclient.PermissionDeniedException;
import de.marcschuler.webrtcserver.service.PermissionService;
import de.marcschuler.webrtcserver.webclient.messages.ErrorMessage;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientServerLeaveEvent;
import de.marcschuler.webrtcserver.webclient.messages.connection.ClientKickEvent;
import org.jspecify.annotations.NonNull;
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
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelJoinEvent;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelLeaveEvent;
import de.marcschuler.webrtcserver.webclient.messages.connection.KickedEvent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionService extends TextWebSocketHandler {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthService authService;
    private final PermissionService permissionService;

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
        send(client, new AuthChallengeRequest(authService.createChallenge()));
    }

    @Override
    public synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("Received message {} from {}", new String(message.asBytes()), session);
        var event = objectMapper.readValue(message.asBytes(), MessageBody.class);
        var client = clientFromSession(session).orElseThrow(() -> new NoClientException("Client for session " + session.getId() + " not found"));

        if (!client.getState().isInteractionAllowed()) {
            if (!allowedEventsWhenUnauthorized.contains(event.getClass())) {
                log.warn("Client tried to use event without being logged in {}", event.getClass());
                kickClient(client, KickReason.UNAUTHORIZED_REQUEST, null);
            }
        }

        log.debug("Sending event {} to bus", event.getClass().getSimpleName());
        try {
            applicationEventPublisher.publishEvent(new ClientMessage<>(event, client, LocalDateTime.now()));
        } catch (PermissionDeniedException e) {
            log.info("User did not have permission to {}: {}", e.getMessage(), e.getMessage());
            log.debug("Exception was", e);
            send(client, new ErrorMessage("No permission for '" + e.getPermissionType() + "'"));
        } catch (ClientKickException e) {
            log.info("Kicking client. Reason: {}", e.getMessage());
            log.debug("Exception was", e);
            kickClient(client, e.getReason(), null);
        } catch (Exception e) {
            log.error("Uncaught exception while handling a message from client '{}'", client, e);
            throw new RuntimeException(e);
        }
    }

    public void joinChannel(@NonNull WebClient client, @NonNull Channel channel) throws PermissionDeniedException {
       permissionService.checkAccess(client.getUser(),
               client.getChannel(),
               PermissionType.CHANNEL_JOIN);

        client.setChannel(channel);
        sendToAll(new ClientChannelJoinEvent(
                serverMapper.mapToDTO(client.getUser()),
                channel.getId()
        ));
    }

    public void leaveChannel(@NonNull WebClient client) {
        if (client.getChannel() == null) {
            log.warn("Client {} already is in no channel", client);
            return;
        }
        client.setChannel(null);
        sendToAll(new ClientChannelLeaveEvent(serverMapper.mapToDTO(client.getUser())));
    }


    /**
     * Kicks a client
     *
     * @param client the client to kick
     * @param reason the reason. May be null
     */
    public void kickClient(WebClient client, KickReason reason, String message) {
        client.setState(WebClientState.INVALID);
        log.info("Kicking client {} for reason: {}", client, reason);
        try {
            send(client, new KickedEvent(reason, message));
        } catch (Exception e) { //we can ignore this
            log.warn("Kicking message failed", e);
        }
        sessions.remove(client);
        if (client.getUser() != null) {
            sendToAll(new ClientKickEvent(serverMapper.mapToDTO(client.getUser()), reason, message));
        }
        try {
            client.getSession().close(); //TODO check if we can ignore the IOException
        } catch (IOException e) {
            log.error("Could not close session", e);
        }
    }

    public void send(WebClient client, MessageBody messageBody) {
        var data = objectMapper.writeValueAsBytes(messageBody);
        log.debug("Sending to client {}: {}", client, new String(data));
        try {
            client.getSession().sendMessage(new TextMessage(data));
        } catch (IOException e) {
            log.error("Could not send message to client: {}", client, e);
            kickClient(client, KickReason.INTERNAL_ERROR, null);
        }
    }

    public void sendToAll(MessageBody messageBody) {
        send(sessions, messageBody);
    }

    public void send(Predicate<WebClient> predicate, MessageBody messageBody) {
        clientsInteractable().stream()
                .filter(predicate)
                .forEach(c -> send(c, messageBody));
    }

    public void send(List<WebClient> clients, MessageBody messageBody) {
        clients.parallelStream()
                .forEach(c -> send(c, messageBody));
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session for ws:{} closed because {}", session.getId(), status);
        clientFromSession(session)
                .ifPresent(client -> {
                    sessions.remove(client);
                    leaveChannel(client);
                    if (client.getUser() != null)
                        sendToAll(new ClientServerLeaveEvent(client.getUser().getId()));
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
