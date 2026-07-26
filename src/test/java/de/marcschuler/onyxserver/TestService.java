package de.marcschuler.onyxserver;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.onyxserver.config.SecurityConfig;
import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.Group;
import de.marcschuler.onyxserver.data.Section;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.repository.ChannelRepository;
import de.marcschuler.onyxserver.repository.GroupRepository;
import de.marcschuler.onyxserver.repository.SectionRepository;
import de.marcschuler.onyxserver.repository.UserRepository;
import de.marcschuler.onyxserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {
    public static final UUID SERVER_ID = UUID.fromString("b4f695b7-693f-40dd-9c24-b86ada4e35b7");

    public static final String USER_ADMIN_ID = "0uCc7RQ9xG4xKlRCUNVdc6dsmFs6cCl2SswW1KYFN38";

    public static final String USER_USER_ID = "Q69PBMwnjhAPl_Pr6OIruDK5Cb6E1C0bGvkZz3YvKI8";

    public static final UUID CHANNEL_LOBBY_ID = UUID.fromString("873676dc-0039-4e71-940d-f1005413cbf3");

    public static final UUID SECTION_TALK_ID = UUID.fromString("0294a6f4-e762-43bc-b346-38e3464757b4");
    public static final UUID SECTION_LOBBY_ID = UUID.fromString("fd821c68-801d-4c25-8a5b-edb997f2c345");

    public static final UUID GROUP_ADMIN_ID = UUID.fromString("a08f02d4-bd84-488c-adca-4dae73cc3f20");

    public static final UUID CHAT_LOBBY_ID = UUID.fromString("510f6de6-19c7-4075-93ae-4456da6912ee");

    public static final UUID SECTION_CHAT_ID = UUID.fromString("64d48960-f666-4a34-aea5-a075d80a86f7");

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final SectionRepository sectionRepository;
    private final GroupRepository groupRepository;
    private final AuthService authService;

    public Channel channelLobby() {
        return channelRepository.findById(CHANNEL_LOBBY_ID).orElseThrow();
    }

    public User userAdmin() {
        return userRepository.findById(USER_ADMIN_ID).orElseThrow();
    }

    public User userUser(){return userRepository.findById(USER_USER_ID).orElseThrow();}

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

    public String createJwtToken(User user) throws JOSEException {
        return authService.createJWT(user);
    }

    public String bearerToken(User user) {
        try {
            return "Bearer " + createJwtToken(user);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
