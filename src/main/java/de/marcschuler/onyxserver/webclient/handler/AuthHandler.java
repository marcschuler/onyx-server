package de.marcschuler.onyxserver.webclient.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.config.WebRTConfig;
import de.marcschuler.onyxserver.dto.AuthChallengeProof;
import de.marcschuler.onyxserver.error.webclient.ClientKickException;
import de.marcschuler.onyxserver.error.webclient.ClientSecurityException;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.AuthService;
import de.marcschuler.onyxserver.service.CryptoService;
import de.marcschuler.onyxserver.service.ServerService;
import de.marcschuler.onyxserver.service.UserService;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.service.websocket.WebSocketService;
import de.marcschuler.onyxserver.webclient.KickReason;
import de.marcschuler.onyxserver.webclient.WebClientState;
import de.marcschuler.onyxserver.webclient.messages.auth.AuthSuccessMessage;
import de.marcschuler.onyxserver.webclient.messages.client.ClientServerJoinEvent;
import de.marcschuler.onyxserver.webclient.messages.peer.IceServerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

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
    private final ServerService serverService;

    @EventListener
    public void onLogin(WebSocketConnectionService.PreliminaryMessage event) throws JOSEException {

        var server = serverService.defaultServer();
        if (server.getSettings().getPassword() != null) {
            log.debug("validating server password");
            var matches = cryptoService.passwordMatches(event.body().serverPassword(), server.getSettings().getPassword());
            if (!matches)
                throw new ClientKickException("Wrong server password", KickReason.UNAUTHORIZED_REQUEST);
        }

        log.info("Responding to auth challenge");
        var content = event.body().challenge();
        var username = event.body().username();

        OctetKeyPair publicKey;
        try {
            publicKey = cryptoService.importPublicKey(event.body().publicKey());
        } catch (ParseException e) {
            throw new ClientKickException("Could not parse JWK", KickReason.INTERNAL_ERROR, e);
        }
        AuthChallengeProof proof;
        try {
            proof = cryptoService.verifyContent(content, AuthChallengeProof.class, publicKey);
        } catch (JacksonException | SignatureException | ParseException e) {
            throw new ClientKickException("Could not verify signature", KickReason.UNAUTHORIZED_REQUEST, e);
        }
        try {
            authService.validateProof(proof);
        } catch (ClientSecurityException e) {
            throw new ClientKickException(e.getMessage(), KickReason.UNAUTHORIZED_REQUEST, e);
        }

        var keyId = cryptoService.generateKeyId(publicKey);
        if (webSocketConnectionService.clientFromKeyId(keyId).isPresent()) {
            var existingUsername = webSocketConnectionService.clientFromKeyId(keyId).get().getUser().getUsername();
            log.info("Client {} is already connected as {}. Kicking new instance", event.body().username(), existingUsername);
            throw new ClientKickException("Client is already connected", KickReason.ALREADY_CONNECTED);
        }
        var user = userService.findById(keyId)
                .orElseGet(() -> userService.registerUser(username, publicKey));
        log.info("User connected: {} ({} formerly known as {})", keyId, username, user.getUsername());
        if (user.isBanned()) {
            var reason = user.getBan().getReason();
            throw new ClientKickException(reason != null ? "You are banned from this server: " + reason : "You are banned from this server", KickReason.BANNED);
        }

        var client = webSocketConnectionService.addClient(event.connection().session(), user);
        user.setUsername(username);
        user.setLastSeen(Instant.now());
        client.setState(WebClientState.LOGGED_IN);
        userService.save(user);

        var authSuccessEvent = new AuthSuccessMessage(null,
                authService.createJWT(user),
                serverMapper.mapToDTO(user),
                webSocketConnectionService.clients().stream()
                        .filter(c -> !user.getId().equals(c.getUser().getId()))
                        .map(serverMapper::mapToDTO)
                        .toList()
        );
        client.send(authSuccessEvent);

        //Send ICE config
        var iceServerData = new IceServerMessage(serverMapper.mapToDTO(webRTConfig.getConfig().getIce()));
        client.send(iceServerData);

        // send join messages to all old clients
        var joinEvent = new ClientServerJoinEvent(serverMapper.mapToDTO(user));
        webSocketConnectionService.clients().stream()
                .filter(c -> c != client)
                .forEach(c -> c.send(joinEvent));

        //TODO deprecated
        var serverTreeChangeEvent = webSocketService.createServerTreeChangeEvent(client);
        client.send(serverTreeChangeEvent);
    }

}
