package de.marcschuler.webrtcserver.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.Event;
import de.marcschuler.webrtcserver.webclient.events.ServerInfoEvent;
import lombok.Data;
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
public class WebSocketSignalingService extends TextWebSocketHandler {

    private final ServerInfoService serverInfoService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final List<WebClient> sessions = new Vector<>();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public synchronized void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("Added ws:{} from {}", session.getId(), session.getRemoteAddress());
        sessions.add(new WebClient(session));
        sendUpdateServerTree();
    }

    @Override
    public synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message {} from {}", new String(message.asBytes()), session);
        var event = new ObjectMapper().readValue(message.asBytes(), Event.class);
        var client = clientFromSession(session).get();

        log.info("Sending event {} to bus",event.getType());
        applicationEventPublisher.publishEvent(new ClientEvent<>(event,client));
    }

    public void moveClient(WebClient client, Channel channel) {
        client.channel = channel;
        sendUpdateServerTree();
    }

    public void sendUpdateServerTree() {
        var serverInfo = new ServerInfoEvent(serverInfoService.getServerInfo(), sessions);
        serverInfo.setType(Event.EventType.SERVER_INFO_TREE);
        for (WebClient session : this.sessions) {
            try {
                sendToClient(session, serverInfo);
            } catch (IOException e) {
                log.error("Could not send to client {}", session, e);
            }
        }
    }

    public void sendToClient(WebClient client, Event event) throws IOException {
        var data = new ObjectMapper().writeValueAsBytes(event);
        client.session.sendMessage(new TextMessage(data));
    }

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session for ws:{} closed because {}", session.getId(), status);
        sessions.remove(clientFromSession(session).get());
        sendUpdateServerTree();
    }


    public Optional<WebClient> clientFromId(@NonNull String id) {
        return this.sessions.stream()
                .filter(s -> s.session.getId().equals(id))
                .findFirst();
    }

    public Optional<WebClient> clientFromSession(@NonNull WebSocketSession session) {
        return this.sessions.stream()
                .filter(s -> s.session == session)
                .findFirst();
    }

    @Data
    public static class WebClient {
        @JsonIgnore
        private final WebSocketSession session;

        private String username;
        private boolean verified;

        private Channel channel;
    }
}
