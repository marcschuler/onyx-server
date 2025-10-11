package de.marcschuler.webrtcserver.webclient.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.config.WebRTConfig;
import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.service.UserService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.events.ClientMessage;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthSuccessMessage;
import de.marcschuler.webrtcserver.webclient.events.client.ClientChannelJoinMessage;
import de.marcschuler.webrtcserver.webclient.events.peer.IceServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientAuthHandler {

    private final WebSocketConnectionService webSocketConnectionService;
    private final WebSocketService webSocketService;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final UserService userService;

    private final ServerMapper serverMapper;

    private final WebRTConfig webRTConfig;

    @EventListener
    public void onLogin(ClientMessage<AuthChallengeResponse> event) throws IOException {
        log.info("Responding to auth challenge");
        var content = event.getBody().getChallenge();
        var username = event.getBody().getUsername();
        PublicKey publicKey;
        try {
            publicKey = cryptoService.parsePublicKey(JWK.parse(event.getBody().getPublicKey().toString()));
        } catch (InvalidKeySpecException | JOSEException e) {
            log.warn("Invalid public key from client {}", event.getClient());
            throw new RuntimeException("Could not parse public key", e);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse JWK", e);
        }
        AuthService.AuthChallenge challenge;
        try {
            challenge = cryptoService.verifyContent(content, AuthService.AuthChallenge.class, publicKey);
        } catch (InvalidKeyException | JsonProcessingException | SignatureException | NoSuchAlgorithmException e) {
            log.warn("Client signature could not be verified {}", event.getClient());
            throw new RuntimeException("Could not verify signature", e);
        }
        if (!authService.isValidChallenge(challenge.getChallenge())) {
            log.warn("Client did not send a valid challenge to authenticate");
            return;
        }

        var keyId = cryptoService.generateKeyId(publicKey);
        if (webSocketConnectionService.clientFromKeyId(keyId).isPresent()) {
            log.info("Client {} is already connected. Kicking new instance", event.getClient().getUser().getUsername());
            webSocketConnectionService.kickClient(event.getClient(), KickReason.ALREADY_CONNECTED);
            return;
        }
        var user = userService.findById(keyId)
                .orElseGet(() -> {
                    log.info("New user connected: {} ({})", keyId, username);
                    var u = new User();
                    u.setId(keyId);
                    u.setPublicKey(publicKey);
                    u.setKnownSince(Instant.now());
                    u.setState(ClientState.INVITATION_PENDING);
                    return u;
                });
        log.info("User connected: {} ({} formerly known as {})", keyId, username, user.getUsername());
        user.setUsername(username);
        event.getClient().setUser(user);
        event.getClient().setState(WebClientState.LOGGED_IN);
        userService.save(user);

        var authSuccessEvent = new AuthSuccessMessage();
        authSuccessEvent.setJwt(authService.createJWT(user));
        event.getClient().sendMessage(authSuccessEvent);

        //Send ICE config
        var iceServerData = new IceServerMessage();
        iceServerData.setIceServers(serverMapper.mapToDTO(webRTConfig.getConfig().getIce()));
        event.getClient().sendMessage(iceServerData);

        var serverTreeChangeEvent = webSocketService.createServerTreeChangeEvent(event.getClient());
        event.getClient().sendMessage(serverTreeChangeEvent);

        for (WebClient client : webSocketConnectionService.clients()) {
            if (client != event.getClient() && client.getChannel() != null) {
                webSocketConnectionService.sendToClient(event.getClient(), new ClientChannelJoinMessage(
                        serverMapper.mapToDTO(client.getUser()),
                        client.getChannel().getId()
                ));
            }
        }
    }

}
