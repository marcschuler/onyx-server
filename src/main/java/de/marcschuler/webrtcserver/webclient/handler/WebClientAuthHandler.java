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
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
import de.marcschuler.webrtcserver.service.webclient.WebClientDataService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthSuccessEvent;
import de.marcschuler.webrtcserver.webclient.events.client.ClientChannelJoinEvent;
import de.marcschuler.webrtcserver.webclient.events.peer.IceServerData;
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

    private final WebClientConnectionService webClientConnectionService;
    private final WebClientDataService webClientDataService;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final UserService userService;

    private final ServerMapper serverMapper;

    private final WebRTConfig webRTConfig;

    @EventListener
    public void onLogin(ClientEvent<AuthChallengeResponse> event) throws IOException {
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
        if (webClientConnectionService.clientFromKeyId(keyId).isPresent()) {
            log.info("Client {} is already connected. Kicking new instance", event.getClient().getUser().getUsername());
            webClientConnectionService.kickClient(event.getClient(), KickReason.ALREADY_CONNECTED);
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

        var authSuccessEvent = new AuthSuccessEvent();
        authSuccessEvent.setJwt(authService.createJWT(user));
        webClientConnectionService.sendToClient(event.getClient(),authSuccessEvent);

        //Send ICE config
        var iceServerData = new IceServerData();
        iceServerData.setIceServers(serverMapper.mapToDTO(webRTConfig.getConfig().getIce()));
        webClientConnectionService.sendToClient(event.getClient(), iceServerData);

        var serverTreeChangeEvent = webClientDataService.createServerTreeChangeEvent(event.getClient());
        webClientConnectionService.sendToClient(event.getClient(), serverTreeChangeEvent);

        for (WebClient client : webClientConnectionService.clients()) {
            if (client != event.getClient() && client.getChannel() != null) {
                webClientConnectionService.sendToClient(event.getClient(), new ClientChannelJoinEvent(
                        serverMapper.mapToDTO(client.getUser()),
                        client.getChannel().getId()
                ));
            }
        }
    }

}
