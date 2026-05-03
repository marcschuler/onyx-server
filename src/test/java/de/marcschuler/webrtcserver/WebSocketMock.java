package de.marcschuler.webrtcserver;

import com.nimbusds.jose.jwk.OctetKeyPair;
import lombok.Getter;
import tools.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNull;

@RequiredArgsConstructor
@Slf4j
public class WebSocketMock {

    private final ObjectMapper objectMapper;

    private WebSocketSession session;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    @Getter
    private final OctetKeyPair keyPair;

    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> responseFuture = new CompletableFuture<>();

        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession s) throws Exception {
                log.info("connected to server");
                responseFuture.complete(s);
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                messageQueue.offer(message.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                log.error("Error: Connection closed", exception);
                responseFuture.completeExceptionally(exception);
            }
        };
        log.info("waiting for connection");
        client.execute(handler, "ws://localhost:8080/websocket").get(2, TimeUnit.SECONDS);
        this.session = responseFuture.get(5, TimeUnit.SECONDS);
    }

    public void sendMessage(MessageBody message) {
        sendMessage(objectMapper.writeValueAsString(message));
    }


    public void sendMessage(String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Receive the next message
    public <T extends MessageBody> T recv() throws InterruptedException, JacksonException {
        return recv(List.of());
    }

    // Receive the next message, ignoring this ones
    public <T extends MessageBody> T recv(List<Class<? extends MessageBody>> ignoredMessages) throws InterruptedException, JacksonException {
        while (true) {
            if (!session.isOpen())
                throw new IllegalStateException("Connection closed");
            var m = messageQueue.poll(5, TimeUnit.SECONDS);
            if (m == null)
                throw new IllegalStateException("No message in queue - was connection closed?");
            T t = (T) objectMapper.readValue(m, MessageBody.class);
            if (ignoredMessages.contains(t.getClass())) {
                log.info("ignoring message: {}", t.getClass());
                continue;
            }
            return t;
        }
    }

    public boolean isOpen() {
        return session.isOpen();
    }

    public <T extends MessageBody> T recv(Class<T> wantedMessage) throws InterruptedException, JacksonException {
        return recv(wantedMessage, 1);
    }

    // Receive the next message of a special type
    public <T extends MessageBody> T recv(Class<T> wantedMessage, int maxTries) throws InterruptedException, JacksonException {
        var i = 0;
        while (i < maxTries) {
            var m = messageQueue.poll(5, TimeUnit.SECONDS);
            if (m == null) {
                throw new IllegalStateException("No message in queue");
            }
            T t = (T) objectMapper.readValue(m, MessageBody.class);
            if (t.getClass().equals(wantedMessage))
                return t;
            log.warn("ignoring message {} that is not {}", t.getClass(), wantedMessage);
            i++;
        }
        throw new IllegalStateException("Could not find message");
    }


    public void close() throws IOException {
        session.close();
    }


    public void recvNothing() {
        try {
            var result = messageQueue.poll(3, TimeUnit.SECONDS);
            assertNull(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
