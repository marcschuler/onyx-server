package de.marcschuler.webrtcserver;

import de.marcschuler.webrtcserver.config.SecurityConfig;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.GroupRepository;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    public static final String USER_ADMIN_ID = "0uCc7RQ9xG4xKlRCUNVdc6dsmFs6cCl2SswW1KYFN38";

    public static final UUID CHANNEL_LOBBY_ID = UUID.fromString("873676dc-0039-4e71-940d-f1005413cbf3");

    public static final UUID SECTION_TALK_ID = UUID.fromString("0294a6f4-e762-43bc-b346-38e3464757b4");
    public static final UUID SECTION_LOBBY_ID = UUID.fromString("fd821c68-801d-4c25-8a5b-edb997f2c345");

    public static final UUID GROUP_ADMIN_ID = UUID.fromString("a08f02d4-bd84-488c-adca-4dae73cc3f20");

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final SectionRepository sectionRepository;
    private final GroupRepository groupRepository;

    public Channel channelLobby() {
        return channelRepository.findById(CHANNEL_LOBBY_ID).orElseThrow();
    }

    public User userAdmin() {
        return userRepository.findById(USER_ADMIN_ID).orElseThrow();
    }

    public Section sectionLobby() {
        return sectionRepository.findById(SECTION_LOBBY_ID).orElseThrow();
    }

    public Section sectionTalk() {
        return sectionRepository.findById(SECTION_TALK_ID).orElseThrow();
    }

    public Group groupAdmin() {
        return groupRepository.findById(GROUP_ADMIN_ID).orElseThrow();
    }

    public void setSecurityContext(User user) {
        var auth =
                new UsernamePasswordAuthenticationToken(
                        new SecurityConfig.AuthenticatedUser(user),
                        null,
                        List.of()
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    public void resetSecurityContext(){
        SecurityContextHolder.clearContext();
    }
}
