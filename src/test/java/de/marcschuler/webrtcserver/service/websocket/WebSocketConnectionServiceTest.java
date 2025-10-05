package de.marcschuler.webrtcserver.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.WebSocketMock;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeRequest;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WebSocketConnectionServiceTest {

    private static final String URL = "ws://localhost:8080/websocket";

    private WebSocketMock webSocketMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoService cryptoService;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketMock = new WebSocketMock(objectMapper);
        webSocketMock.connect();
    }

    @AfterEach
    void tearDown() throws IOException {
        webSocketMock.close();
    }

    @Test
    void testLogin() throws InterruptedException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
            var key =cryptoService.generateKeyPair();
            var authChallengeRequest = (AuthChallengeRequest) webSocketMock.recv();

            assertEquals(AuthChallengeRequest.class,authChallengeRequest.getClass(),"Got a challenge");
            assertNotNull(authChallengeRequest.getChallenge(),"Challenge exists");

            var authChallengeResponse = new AuthChallengeResponse();
            authChallengeResponse.setChallenge(cryptoService.signContent(authChallengeRequest.getChallenge(),key.getPrivate()));
            authChallengeResponse.setUsername("marc");
            authChallengeResponse.setPublicKey(cryptoService.exportPublicKeyToJSON(key.getPublic()));
            webSocketMock.sendMessage(authChallengeResponse);

    }

}