package de.marcschuler.webrtcserver.service;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.file.File;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.UserRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChangeEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final GroupService groupService;
    private final WebSocketConnectionService webSocketConnectionService;

    private final ServerMapper serverMapper;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> all() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void setUserAvatar(User user, File f) {
        user.setAvatar(f);
        userRepository.save(user);
        webSocketConnectionService.sendToAll(
                new ClientChangeEvent(serverMapper.mapToDTO(user))
        );
    }


    /**
     * bans an user. if the user is currently online, the connection is closed
     *
     * @param user the user to ban
     */
    public void ban(User user, @Nullable String message) {
        log.info("Banning user {}", user.getId());
        user.setState(ClientState.BANNED);
        save(user);
        var clientOpt = webSocketConnectionService.clientFromKeyId(user.getId());
        if (clientOpt.isPresent()) {
            log.info("Banned user is online - kicking user");
            message = message != null ? message : "Client was banned from the server";
            webSocketConnectionService.kickClient(clientOpt.get(), KickReason.BANNED, message);
        }
    }


    public User registerUser(String username, OctetKeyPair publicKey) {
        var keyId = cryptoService.generateKeyId(publicKey);
        log.info("New user connected: {} ({})", keyId, username);
        var user = new User();
        user.setId(keyId);
        user.setUsername(username);
        user.setPublicKey(publicKey);
        user.setKnownSince(Instant.now());
        user.setState(ClientState.ACTIVE);

        //set default groups
        var groups = groupService.getGroupsDefaultForNewUser();
        user.setGroups(new HashSet<>(groups));

        userRepository.save(user);
        return user;
    }
}
