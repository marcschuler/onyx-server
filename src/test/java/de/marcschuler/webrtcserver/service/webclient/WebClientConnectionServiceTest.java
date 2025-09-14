package de.marcschuler.webrtcserver.service.webclient;

import com.google.common.util.concurrent.ListenableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebClientConnectionServiceTest {

    private static final String URL = "ws://localhost:8080/websocket";

    private WebSocketClient webSocketClient;

    @BeforeEach
    void setUp() {
        webSocketClient = new StandardWebSocketClient();
    }

    @Test
    void testLogin() throws ExecutionException, InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);

        StandardWebSocketClient client = new StandardWebSocketClient();

        ListenableFuture<WebSocketSession> futureSession =
                client.execute(new WebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        System.out.println("Connected to server");
                        session.sendMessage(new TextMessage("Hello from execute() client!"));
                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        System.out.println("Received: " + message.getPayload());
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
                }, URI.create("ws://localhost:8080/websocket"));

        // block until connected
        WebSocketSession session = futureSession.get();

        // wait for a message before exiting
        latch.await();

        session.close();
    }

}