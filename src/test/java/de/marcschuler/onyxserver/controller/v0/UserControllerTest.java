package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.dto.data.FileDTO;
import de.marcschuler.onyxserver.dto.data.GroupDTO;
import de.marcschuler.onyxserver.dto.data.UserExtendedDTO;
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

}
