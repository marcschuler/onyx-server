package de.marcschuler.webrtcserver.service.websocket;

import tools.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.WebSocketMock;
import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeRequest;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthSuccessMessage;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class WebSocketConnectionServiceTest {

    private WebSocketMock webSocketMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private AuthService authService;
    @Autowired
    private WebSocketConnectionService webSocketConnectionService;

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
    void testFullLogin() throws InterruptedException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        var key = cryptoService.generateKeyPair();
        var authChallengeRequest = (AuthChallengeRequest) webSocketMock.recv();

        //Connected
        assertEquals(AuthChallengeRequest.class, authChallengeRequest.getClass(), "Got a challenge");
        assertNotNull(authChallengeRequest.getChallenge(), "Challenge exists");

        assertEquals(1, webSocketConnectionService.clients().size(),"client found");
        assertEquals(0, webSocketConnectionService.clientsInteractable().size(),"client not interactab.e");
        var me =webSocketConnectionService.clients().get(0);
        assertEquals(WebClientState.NOT_AUTHORIZED,me.getState(),"client not authorized");

        //Authenticate
        var authChallengeResponse = new AuthChallengeResponse();
        authChallengeResponse.setChallenge(cryptoService.signContent(authChallengeRequest.getChallenge(), key.getPrivate()));
        authChallengeResponse.setUsername("marc");
        authChallengeResponse.setPublicKey(cryptoService.exportPublicKeyToJSON(key.getPublic()));
        webSocketMock.sendMessage(authChallengeResponse);

        //Success
        var authSuccessMessage = (AuthSuccessMessage) webSocketMock.recv();
        assertEquals(AuthSuccessMessage.class, authSuccessMessage.getClass(), "Got a success message");
        assertNotNull(authSuccessMessage.getJwt(), "jwt exists");
        assertEquals(cryptoService.generateKeyId(key.getPublic()), authService.verifyJWT(authSuccessMessage.getJwt()), "jwt is valid");

        assertEquals(1, webSocketConnectionService.clients().size(),"client found");
        assertEquals(1, webSocketConnectionService.clientsInteractable().size(),"client interactable");
        me = webSocketConnectionService.clients().get(0);
        assertEquals("marc",me.getUser().getUsername(),"user is right");
        assertEquals(cryptoService.generateKeyId(key.getPublic()),me.getUser().getId(),"is is derived from key");
        assertEquals(WebClientState.LOGGED_IN,me.getState(),"state is logged in");
        assertNull(me.getChannel(),"no channel is set");
        assertNotNull(me.getSession(),"session is available");

        webSocketMock.close();
        assertEquals(0,webSocketConnectionService.clients().size(),"no one connected");
    }

    @Test
    void testLoginWrongKeys() throws InterruptedException, IOException {
        var key = cryptoService.generateKeyPair();
        webSocketMock.recv();

        assertEquals(1, webSocketConnectionService.clients().size(),"client found");
        assertEquals(0, webSocketConnectionService.clientsInteractable().size(),"client not interactab.e");
        var me =webSocketConnectionService.clients().get(0);
        assertEquals(WebClientState.NOT_AUTHORIZED,me.getState(),"client not authorized");

        //Authenticate
        var authChallengeResponse = new AuthChallengeResponse();
        authChallengeResponse.setChallenge(new SignedContent(null,null));
        authChallengeResponse.setUsername("marc");
        authChallengeResponse.setPublicKey(cryptoService.exportPublicKeyToJSON(key.getPublic()));
        webSocketMock.sendMessage(authChallengeResponse);
        Thread.sleep(1000);
        assertFalse(webSocketMock.isOpen());
    }

}