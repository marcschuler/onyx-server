package de.marcschuler.onyxserver.service;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.data.UserBan;
import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.repository.UserRepository;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.KickReason;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChangeEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
     * @param user     the user to ban
     * @param message  the reason, shown to the user. may be null
     * @param bannedBy the user who issued the ban, null if banned by the system
     */
    public void ban(User user, @Nullable String message, @Nullable User bannedBy) {
        log.info("Banning user {}", user.getId());
        var ban = new UserBan();
        ban.setReason(message);
        ban.setBannedAt(Instant.now());
        ban.setBannedBy(bannedBy);
        user.setBan(ban);
        save(user);
        var clientOpt = webSocketConnectionService.clientFromKeyId(user.getId());
        if (clientOpt.isPresent()) {
            log.info("Banned user is online - kicking user");
            message = message != null ? message : "Client was banned from the server";
            clientOpt.get().kick(KickReason.BANNED, message);
        }
    }


    /**
     * removes the ban of an user
     *
     * @param user the user to unban
     */
    public void unban(User user) {
        log.info("Unbanning user {}", user.getId());
        user.setBan(null);
        save(user);
    }


    public User registerUser(String username, OctetKeyPair publicKey) {
        var keyId = cryptoService.generateKeyId(publicKey);
        log.info("New user connected: {} ({})", keyId, username);
        var user = new User();
        user.setId(keyId);
        user.setUsername(username);
        user.setPublicKey(publicKey);
        user.setKnownSince(Instant.now());
        user.setPendingAccess(false);

        //set default groups
        var groups = groupService.getGroupsDefaultForNewUser();
        user.setGroups(new HashSet<>(groups));

        userRepository.save(user);
        return user;
    }
}
