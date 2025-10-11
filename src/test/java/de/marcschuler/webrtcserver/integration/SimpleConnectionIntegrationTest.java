package de.marcschuler.webrtcserver.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.marcschuler.webrtcserver.IntegrationHelper;
import de.marcschuler.webrtcserver.WebSocketMock;
import de.marcschuler.webrtcserver.webclient.events.peer.IceServerMessage;
import de.marcschuler.webrtcserver.webclient.events.server.ServerTreeChangeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SimpleConnectionIntegrationTest {

    @Autowired
    private IntegrationHelper integrationHelper;

    private WebSocketMock client;

    @BeforeEach
    void setup() throws IOException, SignatureException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, TimeoutException {
        client = integrationHelper.quickConnect();
    }

    @AfterEach
    void cleanup() throws IOException {
        client.close();
    }

    @Test
    void testServerTree() throws IOException, InterruptedException {
        client.recv(IceServerMessage.class);
        var tree = client.recv(ServerTreeChangeMessage.class);
        assertNotNull(tree,"tree exists");
        assertEquals(3,tree.getSections(),"three sections");
    }

    @Test
    void testChannelJoin() throws InterruptedException, JsonProcessingException {
        client.recv(IceServerMessage.class);
        var tree = client.recv(ServerTreeChangeMessage.class);
    }
}
