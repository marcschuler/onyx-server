package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.dto.InviteResponseDto;
import de.marcschuler.onyxserver.dto.KickRequestDTO;
import de.marcschuler.onyxserver.dto.data.*;
import de.marcschuler.onyxserver.webclient.KickReason;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
@Slf4j
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;
    private RestClient adminClient;

    private static final UUID GROUP_MOD_ID = UUID.fromString("d8ccd166-2556-48c2-b5bd-c7d42995a2db");

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userUser()))
                .build();
        adminClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userAdmin()))
                .build();
    }

    @Test
    void testNoAvatar() {
        var ex = assertThrows(HttpClientErrorException.class, () ->
                restClient.get()
                        .uri("/v0/user/{id}/avatar", TestService.USER_ADMIN_ID)
                        .retrieve()
                        .toBodilessEntity());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testUsers() {
        var users = restClient.get()
                .uri("/v0/user/")
                .retrieve()
                .body(String.class);
        org.junit.jupiter.api.Assertions.assertNotNull(users);
    }

    @Test
    void testBanAndUnban() {
        var banned = adminClient.put()
                .uri("/v0/user/{id}/state/ban", TestService.USER_USER_ID)
                .retrieve()
                .body(UserExtendedDTO.class);
        assertNotNull(banned);
        assertEquals(ClientState.BANNED, banned.getState());

        var unbanned = adminClient.put()
                .uri("/v0/user/{id}/state/unban", TestService.USER_USER_ID)
                .retrieve()
                .body(UserExtendedDTO.class);
        assertNotNull(unbanned);
        assertEquals(ClientState.ACTIVE, unbanned.getState());
    }

    @Test
    void testGroupsAddAndDelete() {
        var groups = adminClient.put()
                .uri("/v0/user/{id}/groups/{groupId}", TestService.USER_USER_ID, GROUP_MOD_ID)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GroupDTO>>() {});
        assertNotNull(groups);
        assertTrue(groups.stream().anyMatch(g -> g.getId().equals(GROUP_MOD_ID)));

        var updatedGroups = adminClient.delete()
                .uri("/v0/user/{id}/groups/{groupId}", TestService.USER_USER_ID, GROUP_MOD_ID)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GroupDTO>>() {});
        assertNotNull(updatedGroups);
        assertFalse(updatedGroups.stream().anyMatch(g -> g.getId().equals(GROUP_MOD_ID)));
    }

    @Test
    void testAvatarUploadAndGet() {
        var parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new ByteArrayResource("fake-avatar-data".getBytes()) {
            @Override
            public String getFilename() {
                return "avatar.png";
            }
        });

        var file = adminClient.post()
                .uri("/v0/user/{id}/profile/avatar", TestService.USER_ADMIN_ID)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .body(FileDTO.class);
        assertNotNull(file);
        assertEquals("avatar.png", file.getFilename());
        assertTrue(file.getSize() > 0);

        var avatar = adminClient.get()
                .uri("/v0/user/{id}/avatar", TestService.USER_ADMIN_ID)
                .retrieve()
                .body(byte[].class);
        assertNotNull(avatar);
        assertTrue(avatar.length > 0);
    }

    @Test
    void testAvatarDelete() {
        var parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new ByteArrayResource("fake-avatar-data".getBytes()) {
            @Override
            public String getFilename() {
                return "avatar.png";
            }
        });

        adminClient.post()
                .uri("/v0/user/{id}/profile/avatar", TestService.USER_ADMIN_ID)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .toBodilessEntity();

        adminClient.delete()
                .uri("/v0/user/{id}/avatar", TestService.USER_ADMIN_ID)
                .retrieve()
                .toBodilessEntity();

        var ex = assertThrows(HttpClientErrorException.class, () ->
                adminClient.get()
                        .uri("/v0/user/{id}/avatar", TestService.USER_ADMIN_ID)
                        .retrieve()
                        .toBodilessEntity());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testActivateUser() {
        var user = restClient.get()
                .uri("/v0/user/")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserExtendedDTO>>() {})
                .stream()
                .filter(u -> TestService.USER_USER_ID.equals(u.getId()))
                .findFirst()
                .orElseThrow();

        if (user.getState() != ClientState.ACTIVE) {
            var activated = adminClient.put()
                    .uri("/v0/user/{id}/state/active", TestService.USER_USER_ID)
                    .retrieve()
                    .body(UserExtendedDTO.class);
            assertNotNull(activated);
            assertEquals(ClientState.ACTIVE, activated.getState());
        }
    }

    @Test
    void testInviteRedemption() {
        var group = new SimpleGroupDTO();
        group.setId(GROUP_MOD_ID);
        group.setName("Mod");

        var inviteDto = new InviteDTO();
        inviteDto.setCode("user-invite");
        inviteDto.setTitle("User Invite");
        inviteDto.setEndDate(LocalDateTime.now().plusDays(7));
        inviteDto.setUsages(0);
        inviteDto.setGroups(List.of(group));

        adminClient.post()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .body(inviteDto)
                .retrieve()
                .toBodilessEntity();

        var response = restClient.put()
                .uri("/v0/user/{id}/invite", TestService.USER_USER_ID)
                .body("user-invite")
                .retrieve()
                .body(InviteResponseDto.class);

        assertNotNull(response);
        assertEquals("User Invite", response.name());
    }

    @Test
    void testKickFromChannelRequiresClientOnline() {
        var kick = new KickRequestDTO();
        kick.setReason(KickReason.OTHER);
        kick.setMessage("test kick");

        assertThrows(Exception.class, () ->
                adminClient.put()
                        .uri("/v0/user/{id}/kick/channel", TestService.USER_USER_ID)
                        .body(kick)
                        .retrieve()
                        .toBodilessEntity());
    }

}
