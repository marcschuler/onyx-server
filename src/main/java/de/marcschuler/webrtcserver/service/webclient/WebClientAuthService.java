package de.marcschuler.webrtcserver.service.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.service.CryptoService;
import de.marcschuler.webrtcserver.service.UserService;
import de.marcschuler.webrtcserver.webclient.WebClientState;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.auth.AuthChallengeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientAuthService {

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final UserService userService;

    @EventListener
    public void onLogin(ClientEvent<AuthChallengeResponse> event) {
        var challenge = event.getBody().getChallenge();
        var username = event.getBody().getUsername();
        PublicKey publicKey;
        try {
            publicKey = cryptoService.parsePublicKey(event.getBody().getPublicKey().getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn("Invalid public key from client {}", event.getClient());
            throw new RuntimeException("Could not parse public key", e);
        }
        if (!authService.isValidChallenge(challenge.getContent().getChallenge())) {
            log.warn("Client did not send a valid challenge to authenticate");
            return;
        }
        try {
            cryptoService.verifyContent(challenge, publicKey);
        } catch (InvalidKeyException | JsonProcessingException | SignatureException | NoSuchAlgorithmException e) {
            log.warn("Client signature could not be verified {}", event.getClient());
            throw new RuntimeException("Could not verify signature", e);
        }
        var keyId = cryptoService.generateKeyId(publicKey);
        var user = userService.findById(keyId)
                .orElseGet(() -> {
                    log.info("New user connected: {} ({})", keyId, username);
                    var u = new User();
                    u.setId(keyId);
                    u.setPublicKey(publicKey);
                    u.setKnownSince(Instant.now());
                    return u;
                });
        log.info("User connected: {} ({} formerly known as {})", keyId, username, user.getUsername());
        user.setUsername(username);
        event.getClient().setUser(user);
        event.getClient().setState(WebClientState.LOGGED_IN);
        userService.save(user);
    }

}
