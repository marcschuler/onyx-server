package de.marcschuler.onyxserver.service.websocket;

import de.marcschuler.onyxserver.Util;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.error.webclient.ClientKickException;
import de.marcschuler.onyxserver.error.webclient.NoClientException;
import de.marcschuler.onyxserver.error.webclient.PermissionDeniedException;
import de.marcschuler.onyxserver.service.AuthService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.KickReason;
import de.marcschuler.onyxserver.webclient.WebClient;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthChallengeRequest;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.onyxserver.webclient.messages.client.ClientServerLeaveEvent;
import de.marcschuler.onyxserver.webclient.messages.connection.KickedEvent;
import de.marcschuler.onyxserver.webclient.messages.error.NoPermissionMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketConnectionService extends TextWebSocketHandler {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthService authService;

    private final ObjectMapper objectMapper;

    @Value("${onyx.auth.expire-login}")
    private Duration expireLogin;

    private final List<PreliminaryConnection> preliminaryConnections = new Vector<>();
    private final List<WebClient> clients = new Vector<>();


    @Override
    public synchronized void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("Added websocket {} from {}", session.getId(), session.getRemoteAddress());
        var connection = new PreliminaryConnection(LocalDateTime.now(), session);
        connection.send(new AuthChallengeRequest(authService.createChallenge()));
        this.preliminaryConnections.add(connection);
    }

    @Scheduled(fixedRate = 15_000)
    public synchronized void cleanPreliminaryConnections() {
        var cutoff = LocalDateTime.now().minus(expireLogin);
        preliminaryConnections.stream()
                .filter(connection -> connection.created().isBefore(cutoff))
                .toList()
                .forEach(connection -> {
                    preliminaryConnections.remove(connection);
                    try {
                        connection.session().close();
                    } catch (IOException e) {
                        log.error("Could not close preliminary session {}", connection.session().getId(), e);
                    }
                });
    }

    /**
     * adds a new client and removes if from preliminary list
     *
     * @param session the session
     * @param user    the associated user
     * @return
     */
    public WebClient addClient(WebSocketSession session, User user){
        log.info("Added new client {}", user.getUsername());
        this.preliminaryConnections.removeIf(c -> c.session==session);
        var client = new WebClient(this,session,user);
        this.clients.add(client);
        return client;
    }

    @Override
    public synchronized void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Received message {} from {}", new String(message.asBytes()), session);
        var event = objectMapper.readValue(message.asBytes(), MessageBody.class);

        if (event.getClass().equals(AuthChallengeResponse.class)) {
            var connection = preliminaryConnections.stream()
                    .filter(c -> c.session == session)
                    .findFirst().orElseThrow();
            try {
                applicationEventPublisher.publishEvent(new PreliminaryMessage((AuthChallengeResponse) event, connection));
            } catch (ClientKickException e) {
                log.info("Kicking preliminary connection. Reason: {}", e.getMessage());
                preliminaryConnections.remove(connection);
                try {
                    connection.send(new KickedEvent(e.getReason(), e.getMessage()));
                    connection.session().close();
                } catch (IOException ex) {
                    log.error("Could not kick preliminary connection {}", session.getId(), ex);
                }
            }
            return;
        }

        var client = clientFromSession(session).orElseThrow(() -> new NoClientException("Client for session " + session.getId() + " not found"));
        if (!client.getState().isInteractionAllowed()) {
            log.warn("Client tried to use event without being allowed to {}", event.getClass());
            client.kick(KickReason.UNAUTHORIZED_REQUEST, null);
        }

        log.debug("Sending event {} to bus", event.getClass().getSimpleName());
        try {
            applicationEventPublisher.publishEvent(new ClientMessage<>(event, client, LocalDateTime.now()));
        } catch (PermissionDeniedException e) {
            log.info("User did not have permission to {}: {}", e.getMessage(), e.getMessage());
            log.debug("Exception was", e);
            client.send(new NoPermissionMessage(e.getPermissionType(), null));
        } catch (ClientKickException e) {
            log.info("Kicking client. Reason: {}", e.getMessage());
            log.debug("Exception was", e);
            client.kick(e.getReason(), null);
        } catch (Exception e) {
            log.error("Uncaught exception while handling a message from client '{}'", client, e);
            throw new RuntimeException(e);
        }
    }

    public void remove(WebClient client) {
        clients.remove(client);
    }

    public void sendToAll(MessageBody messageBody) {
        clients.parallelStream()
                .forEach(client -> client.send(messageBody));
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session for ws:{} closed because {}", session.getId(), status);
        clientFromSession(session)
                .ifPresent(client -> {
                    clients.remove(client);
                    client.leaveChannel();
                    if (client.getUser() != null)
                        sendToAll(new ClientServerLeaveEvent(client.getUser().getId()));
                });
    }


    public Optional<WebClient> clientFromSessionId(@NonNull String id) {
        return this.clients.stream()
                .filter(s -> s.getSession().getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromKeyId(@NotNull String id) {
        // User might be null if a client is connected for the first time but hasn't authenticated
        return this.clients.stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromSession(@NonNull WebSocketSession session) {
        return this.clients.stream()
                .filter(s -> s.getSession() == session)
                .findFirst();
    }

    public List<WebClient> clients() {
        return this.clients;
    }

    public List<User> users() {
        return clients().stream()
                .map(WebClient::getUser)
                .filter(Objects::nonNull)
                .toList();
    }

    public record PreliminaryConnection(LocalDateTime created, WebSocketSession session) {

        void send(MessageBody message) throws IOException {
            this.session.sendMessage(new TextMessage(Util.objectMapper.writeValueAsBytes(message)));
        }
    }

    public record PreliminaryMessage(AuthChallengeResponse body, PreliminaryConnection connection) {

    }
}
