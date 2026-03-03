package de.marcschuler.webrtcserver;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.SectionRepository;
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
    private final SectionRepository sectionRepository;

    public Channel channelLobby(){
        return channelRepository.findById(UUID.fromString("873676dc-0039-4e71-940d-f1005413cbf3")).orElseThrow();
    }

    public User userAdmin(){
        return  userRepository.findById(USER_ADMIN_ID).orElseThrow();
    }
    public Section sectionLobby(){
        return  sectionRepository.findById(UUID.fromString("fd821c68-801d-4c25-8a5b-edb997f2c345")).orElseThrow();
    }
    public Section sectionTalk(){
        return sectionRepository.findById(UUID.fromString("0294a6f4-e762-43bc-b346-38e3464757b4")).orElseThrow();
    }
}
