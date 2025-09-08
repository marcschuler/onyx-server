package de.marcschuler.webrtcserver.service.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
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
import de.marcschuler.webrtcserver.webclient.events.client.ClientChannelJoinEvent;
import de.marcschuler.webrtcserver.webclient.events.client.ClientChannelLeaveEvent;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

    private final ObjectMapper objectMapper;

    private final ServerMapper serverMapper;

    private final List<WebClient> sessions = new Vector<>();

    private final List<Class<? extends EventBody>> allowedEventsWhenUnauthorized = List.of(AuthChallengeResponse.class);

    @PostConstruct
    public void init() {
        var names = new ArrayList<String>();
        new Reflections("de.marcschuler.webrtcserver.webclient.events")
                .getSubTypesOf(EventBody.class)
                .stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .filter(c -> !Modifier.isInterface(c.getModifiers()))
                .forEach(c -> {
                    names.add(c.getSimpleName());
                    objectMapper.registerSubtypes(new NamedType(c, c.getSimpleName()));
                });
        log.info("Initialised events {}", names);
    }

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
        var event = objectMapper.readValue(message.asBytes(), EventBody.class);
        var client = clientFromSession(session).get();

        if (!client.getState().isInteractionAllowed()) {
            if (!allowedEventsWhenUnauthorized.contains(event.getClass())) {
                log.warn("Client tried to use event without being logged in {}", event.getClass());
                kickClient(client, KickReason.UNAUTHORIZED_REQUEST);
            }
        }

        log.info("Sending event {} to bus", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(new ClientEvent<>(event, client));
    }

    public void moveClient(WebClient client, Channel channel) {
        var channelBefore = client.getChannel();
        client.setChannel(channel);

        if (channel!=null){
            sendToAllClients(new ClientChannelJoinEvent(
                    serverMapper.mapToDTO(client.getUser()),
                    channel.getId()
            ));
        }else if (channelBefore != null){
            sendToAllClients(new ClientChannelLeaveEvent(serverMapper.mapToDTO(client.getUser())));
        }else{
            log.warn("Ignored channel change request because moving from null to null");
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
        var data = objectMapper.writeValueAsBytes(eventBody);
        client.getSession().sendMessage(new TextMessage(data));
    }

    public void sendToAllClients(EventBody eventBody) {
        sendToClients(sessions, eventBody);
    }

    public void sendToClients(List<WebClient> clients, EventBody eventBody) {
        var exceptions = false;
        for (var client : clients) {
            try {
                sendToClient(client, eventBody);
            } catch (IOException e) {
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
        var client = clientFromSession(session).get();
        sessions.remove(client);
        moveClient(client,null); //TODO may send another event type?
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

    public List<WebClient> clients(){
        return this.sessions;
    }

}
