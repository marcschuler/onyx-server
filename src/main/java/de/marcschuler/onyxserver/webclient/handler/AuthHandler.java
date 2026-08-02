package de.marcschuler.onyxserver.webclient.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.config.WebRTConfig;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.dto.AuthChallenge;
import de.marcschuler.onyxserver.error.webclient.ClientKickException;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.AuthService;
import de.marcschuler.onyxserver.service.CryptoService;
import de.marcschuler.onyxserver.service.UserService;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.service.websocket.WebSocketService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.KickReason;
import de.marcschuler.onyxserver.webclient.WebClientState;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthChallengeResponse;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthSuccessMessage;
import de.marcschuler.onyxserver.webclient.messages.client.ClientServerJoinEvent;
import de.marcschuler.onyxserver.webclient.messages.peer.IceServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

import java.io.IOException;
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
        var content = event.body().challenge();
        var username = event.body().username();
        OctetKeyPair publicKey;
        try {
            publicKey = cryptoService.importPublicKey(event.body().publicKey());
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse JWK", e);
        }
        AuthChallenge challenge;
        try {
            challenge = cryptoService.verifyContent(content, AuthChallenge.class, publicKey);
        } catch (JacksonException | SignatureException | ParseException e) {
            log.warn("Client signature could not be verified {}", event.client());
            throw new RuntimeException("Could not verify signature", e);
        }
        if (!authService.isValidChallenge(challenge.challenge())) {
            log.warn("Client did not send a valid challenge to authenticate");
            return;
        }

        var keyId = cryptoService.generateKeyId(publicKey);
        if (webSocketConnectionService.clientFromKeyId(keyId).isPresent()) {
            var existingUsername = webSocketConnectionService.clientFromKeyId(keyId).get().getUser().getUsername();
            log.info("Client {} is already connected as {}. Kicking new instance", event.body().username(), existingUsername);
            webSocketConnectionService.kickClient(event.client(), KickReason.ALREADY_CONNECTED, null);
            return;
        }
        var user = userService.findById(keyId)
                .orElseGet(() -> userService.registerUser(username, publicKey));
        log.info("User connected: {} ({} formerly known as {})", keyId, username, user.getUsername());
        if (user.getState() == ClientState.BANNED) {
            throw new ClientKickException("User is already banned", KickReason.BANNED);
        }

        user.setUsername(username);
        user.setLastSeen(Instant.now());
        event.client().setUser(user);
        event.client().setState(WebClientState.LOGGED_IN);
        userService.save(user);

        var authSuccessEvent = new AuthSuccessMessage(null,
                authService.createJWT(user),
                serverMapper.mapToDTO(user),
                webSocketConnectionService.clientsInteractable().stream()
                        .filter(c -> !user.getId().equals(c.getUser().getId()))
                        .map(serverMapper::mapToDTO)
                        .toList()
        );
        event.client().sendMessage(authSuccessEvent);

        //Send ICE config
        var iceServerData = new IceServerMessage(serverMapper.mapToDTO(webRTConfig.getConfig().getIce()));
        event.client().sendMessage(iceServerData);

        // send join messages to all old clients
        webSocketConnectionService.send(c -> c != event.client(), new ClientServerJoinEvent(serverMapper.mapToDTO(user)));

        //TODO
        var serverTreeChangeEvent = webSocketService.createServerTreeChangeEvent(event.client());
        event.client().sendMessage(serverTreeChangeEvent);


    }

}
