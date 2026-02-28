package de.marcschuler.webrtcserver;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.UserRepository;
import de.marcschuler.webrtcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    public static final String USER_ADMIN_ID = "0uCc7RQ9xG4xKlRCUNVdc6dsmFs6cCl2SswW1KYFN38";

    private final UserRepository userRepository;

    private final ChannelRepository channelRepository;
    private final UserService userService;

    public Channel channelLobby(){
        return channelRepository.findById(UUID.fromString("873676dc-0039-4e71-940d-f1005413cbf3")).orElseThrow();
    }

    public User userAdmin(){
        return  userRepository.findById(USER_ADMIN_ID).orElseThrow();
    }
}
