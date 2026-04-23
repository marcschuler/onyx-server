package de.marcschuler.webrtcserver.integration;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.webrtcserver.IntegrationHelper;
import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.WebSocketMock;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientServerJoinEvent;
import de.marcschuler.webrtcserver.webclient.messages.peer.IceServerMessage;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerTreeChangeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@OnyxTest
public class SimpleConnectionIntegrationTest {

    @Autowired
    private IntegrationHelper integrationHelper;

    private WebSocketMock client;

    @BeforeEach
    void setup() throws IOException, SignatureException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, TimeoutException, JOSEException {
        client = integrationHelper.quickConnect();
    }

    @AfterEach
    void cleanup() throws IOException {
        client.close();
    }

    @Test
    void testServerTree() throws InterruptedException {
        client.recv(IceServerMessage.class);
        client.recv(ClientServerJoinEvent.class);
        var tree = client.recv(ServerTreeChangeMessage.class);
        assertNotNull(tree,"tree exists");
        assertEquals(3,tree.getSections().size(),"three sections");
    }

    @Test
    void testChannelJoin() throws InterruptedException, JacksonException {
        client.recv(IceServerMessage.class);
        var tree = client.recv(ClientServerJoinEvent.class);
    }
}
