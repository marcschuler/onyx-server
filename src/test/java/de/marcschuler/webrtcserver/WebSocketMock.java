package de.marcschuler.webrtcserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.*;

@RequiredArgsConstructor
public class WebSocketMock {

    private final ObjectMapper objectMapper;

    private WebSocketSession session;
    private String msg;
    private final CountDownLatch latch = new CountDownLatch(1);

    public WebSocketSession connect() throws InterruptedException, ExecutionException, TimeoutException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        var se =  client.execute(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession s) throws Exception {
                System.out.println("Connected to server");
                session = s;
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                System.out.println("Received: " + message.getPayload());
                while(msg!=null){
                    Thread.sleep(10);
                }
                msg = (String) message.getPayload();
                latch.countDown();
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                System.err.println("Transport error: " + exception.getMessage());
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                System.out.println("Connection closed: " + closeStatus);
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, "ws://localhost:8080/websocket");

        return se.get(5, TimeUnit.SECONDS);
    }

    public void sendMessage(String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }

    public <T extends EventBody> T recv() throws InterruptedException, JsonProcessingException {
        latch.await(5,TimeUnit.SECONDS);
        var m = msg;
        msg=null;
        return (T) objectMapper.readValue(m, EventBody.class);
    }

    public void close() throws IOException {
        session.close();
    }

    public void sendMessage(AuthChallengeResponse authChallengeResponse) throws IOException {
        sendMessage(objectMapper.writeValueAsString(authChallengeResponse));
    }
}
