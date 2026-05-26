package de.marcschuler.webrtcserver.webclient.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.webrtcserver.config.WebRTConfig;
import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.error.webclient.ClientKickException;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.service.UserService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.ClientMessage;
import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.messages.auth.AuthSuccessMessage;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientServerJoinEvent;
import de.marcschuler.webrtcserver.webclient.messages.peer.IceServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final WebSocketConnectionService webSocketConnectionService;
    private final WebSocketService webSocketService;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final UserService userService;

    private final ServerMapper serverMapper;

    private final WebRTConfig webRTConfig;

    @EventListener
    public void onLogin(ClientMessage<AuthChallengeResponse> event) throws IOException, JOSEException {
        log.info("Responding to auth challenge");
        var content = event.getBody().getChallenge();
        var username = event.getBody().getUsername();
        OctetKeyPair publicKey;
        try {
            publicKey = cryptoService.importPublicKey(event.getBody().getPublicKey());
        } catch (JOSEException e) {
            log.warn("Invalid public key from client {}", event.getClient());
            throw new RuntimeException("Could not parse public key", e);
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse JWK", e);
        }
        AuthChallenge challenge;
        try {
            challenge = cryptoService.verifyContent(content, AuthChallenge.class, publicKey);
        } catch (InvalidKeyException | JacksonException | SignatureException | NoSuchAlgorithmException |
                 ParseException e) {
            log.warn("Client signature could not be verified {}", event.getClient());
            throw new RuntimeException("Could not verify signature", e);
        }
        if (!authService.isValidChallenge(challenge.getChallenge())) {
            log.warn("Client did not send a valid challenge to authenticate");
            return;
        }

        var keyId = cryptoService.generateKeyId(publicKey);
        if (webSocketConnectionService.clientFromKeyId(keyId).isPresent()) {
            var existingUsername = webSocketConnectionService.clientFromKeyId(keyId).get().getUser().getUsername();
            log.info("Client {} is already connected as {}. Kicking new instance", event.getBody().getUsername(), existingUsername);
            webSocketConnectionService.kickClient(event.getClient(), KickReason.ALREADY_CONNECTED, null);
            return;
        }
        var user = userService.findById(keyId)
                .orElseGet(() -> userService.registerUser(username,publicKey));
        log.info("User connected: {} ({} formerly known as {})", keyId, username, user.getUsername());
        if (user.getState() == ClientState.BANNED) {
            throw new ClientKickException("User is already banned", KickReason.BANNED);
        }

        user.setUsername(username);
        user.setLastSeen(Instant.now());
        event.getClient().setUser(user);
        event.getClient().setState(WebClientState.LOGGED_IN);
        userService.save(user);

        var authSuccessEvent = new AuthSuccessMessage();
        authSuccessEvent.setJwt(authService.createJWT(user));
        authSuccessEvent.setMe(serverMapper.mapToDTO(user));
        authSuccessEvent.setClients(
                webSocketConnectionService.clientsInteractable().stream()
                        .filter(c -> !user.getId().equals(c.getUser().getId()))
                        .map(serverMapper::mapToDTO)
                        .toList()
        );
        event.getClient().sendMessage(authSuccessEvent);

        //Send ICE config
        var iceServerData = new IceServerMessage();
        iceServerData.setIceServers(serverMapper.mapToDTO(webRTConfig.getConfig().getIce()));
        event.getClient().sendMessage(iceServerData);

        // send join messages to all old clients
        webSocketConnectionService.send(c -> c != event.getClient(), new ClientServerJoinEvent(serverMapper.mapToDTO(user)));

        //TODO
        var serverTreeChangeEvent = webSocketService.createServerTreeChangeEvent(event.getClient());
        event.getClient().sendMessage(serverTreeChangeEvent);


    }

}
