package de.marcschuler.webrtcserver;

import tools.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeRequest;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthSuccessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationHelper {
    private final WebSocketConnectionService webSocketConnectionService;
    private final AuthService authService;
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public WebSocketMock quickConnect() throws IOException, SignatureException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, TimeoutException {
        return quickConnect(RandomStringUtils.randomAlphanumeric(8));
    }

    public WebSocketMock quickConnect(String username) throws IOException, SignatureException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, TimeoutException {
        return quickConnect(cryptoService.generateKeyPair(),username);
    }

    public WebSocketMock quickConnect(KeyPair keys, String username) throws ExecutionException, InterruptedException, TimeoutException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        var mock = new WebSocketMock(objectMapper);
        mock.connect();

        var authChallengeRequest = (AuthChallengeRequest) mock.recv();
        var authChallengeResponse = new AuthChallengeResponse();
        authChallengeResponse.setChallenge(cryptoService.signContent(authChallengeRequest.getChallenge(), keys.getPrivate()));
        authChallengeResponse.setUsername(username);
        authChallengeResponse.setPublicKey(cryptoService.exportPublicKeyToJSON(keys.getPublic()));
        mock.sendMessage(authChallengeResponse);
        mock.recv(AuthSuccessMessage.class);
        return mock;
    }
}
