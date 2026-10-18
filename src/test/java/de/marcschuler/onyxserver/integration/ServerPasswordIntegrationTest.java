package de.marcschuler.onyxserver.integration;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.IntegrationHelper;
import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.WebSocketMock;
import de.marcschuler.onyxserver.service.CryptoService;
import de.marcschuler.onyxserver.service.ServerService;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthChallengeRequest;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.onyxserver.webclient.messages.connection.KickedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
public class ServerPasswordIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IntegrationHelper integrationHelper;

    @Autowired
    private ServerService serverService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    private WebSocketMock client;

    @AfterEach
    void cleanup() throws IOException {
        if (client != null)
            client.close();
    }

    @Test
    void testServerPasswordRight() throws Exception {
        serverService.setServerPassword(serverService.defaultServer(), "secret");

        client = integrationHelper.quickConnect("user", port, "secret");

        assertNotNull(client);
        assertTrue(client.isOpen());
    }

    @Test
    void testServerPasswordWrong() throws Exception {
        serverService.setServerPassword(serverService.defaultServer(), "secret");

        OctetKeyPair keyPair = cryptoService.generateKeyPair();
        WebSocketMock mock = new WebSocketMock(objectMapper, keyPair, port);
        mock.connect();
        var authChallengeRequest = (AuthChallengeRequest) mock.recv();
        var authChallengeResponse = new AuthChallengeResponse(keyPair.toPublicJWK().toJSONObject(),
                cryptoService.signContent(authChallengeRequest.challenge(), keyPair),
                "user", "wrong");
        mock.sendMessage(authChallengeResponse);

        KickedEvent kicked = mock.recv(KickedEvent.class);
        assertNotNull(kicked);
        mock.close();
    }
}
