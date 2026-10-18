package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.InviteDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
class InviteControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

    private RestClient adminClient;

    @BeforeEach
    void setUp() {
        adminClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userAdmin()))
                .build();
    }

    @Test
    void testAll() {
        var invites = adminClient.get()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .retrieve()
                .body(new ParameterizedTypeReference<List<InviteDTO>>() {});

        assertNotNull(invites);
        assertTrue(invites.isEmpty());
    }

    @Test
    void testCreate() {
        var dto = new InviteDTO();
        dto.setCode("new-invite");
        dto.setTitle("New Invite");
        dto.setEndDate(LocalDateTime.now().plusDays(7));
        dto.setUsages(0);

        var invite = adminClient.post()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .body(dto)
                .retrieve()
                .body(InviteDTO.class);

        assertNotNull(invite);
        assertEquals("new-invite", invite.getCode());
        assertEquals("New Invite", invite.getTitle());
    }

    @Test
    void testEdit() {
        var dto = new InviteDTO();
        dto.setCode("edit-invite");
        dto.setTitle("Original Title");
        dto.setEndDate(LocalDateTime.now().plusDays(7));
        dto.setUsages(0);

        adminClient.post()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        dto.setTitle("Updated Title");
        var updated = adminClient.put()
                .uri("/v0/server/{serverId}/invites/{inviteId}", TestService.SERVER_ID, "edit-invite")
                .body(dto)
                .retrieve()
                .body(InviteDTO.class);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    void testDelete() {
        var dto = new InviteDTO();
        dto.setCode("delete-invite");
        dto.setTitle("Delete Me");
        dto.setEndDate(LocalDateTime.now().plusDays(7));
        dto.setUsages(0);

        adminClient.post()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        adminClient.delete()
                .uri("/v0/server/{serverId}/invites/{inviteId}", TestService.SERVER_ID, "delete-invite")
                .retrieve()
                .toBodilessEntity();

        var invites = adminClient.get()
                .uri("/v0/server/{serverId}/invites", TestService.SERVER_ID)
                .retrieve()
                .body(new ParameterizedTypeReference<List<InviteDTO>>() {});

        assertNotNull(invites);
        assertTrue(invites.stream().noneMatch(i -> "delete-invite".equals(i.getCode())));
    }
}
