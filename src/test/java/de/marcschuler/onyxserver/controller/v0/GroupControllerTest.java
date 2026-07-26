package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.GroupCreateDTO;
import de.marcschuler.onyxserver.dto.data.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
@Slf4j
class GroupControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    private static final UUID GROUP_ADMIN_ID = UUID.fromString("a08f02d4-bd84-488c-adca-4dae73cc3f20");
    private static final UUID GROUP_MOD_ID = UUID.fromString("d8ccd166-2556-48c2-b5bd-c7d42995a2db");
    private static final UUID GROUP_USER_ID = UUID.fromString("bc4f5758-59cd-49da-8764-0959283e86a7");

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = testService.bearerToken(testService.userAdmin());
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", adminToken)
                .build();
    }

    @Test
    void testAll() {
        var groups = restClient.get()
                .uri("/v0/group")
                .retrieve()
                .body(new ParameterizedTypeReference<List<GroupDTO>>() {});
        assertNotNull(groups);
        assertEquals(3, groups.size());
        assertTrue(groups.stream().anyMatch(g -> g.getId().equals(GROUP_ADMIN_ID)));
        assertTrue(groups.stream().anyMatch(g -> g.getId().equals(GROUP_MOD_ID)));
        assertTrue(groups.stream().anyMatch(g -> g.getId().equals(GROUP_USER_ID)));
    }

    @Test
    void testCreate() {
        var createDTO = new GroupCreateDTO("Test Group", "A test group", false, true);

        var created = restClient.post()
                .uri("/v0/group")
                .body(createDTO)
                .retrieve()
                .body(GroupDTO.class);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Group", created.getName());

        var groups = restClient.get()
                .uri("/v0/group")
                .retrieve()
                .body(new ParameterizedTypeReference<List<GroupDTO>>() {});
        assertNotNull(groups);
        assertTrue(groups.stream().anyMatch(g -> g.getId().equals(created.getId())));
    }

    @Test
    void testCreateAndEdit() {
        var createDTO = new GroupCreateDTO("Editable Group", "Will be edited", false, false);

        var created = restClient.post()
                .uri("/v0/group")
                .body(createDTO)
                .retrieve()
                .body(GroupDTO.class);
        assertNotNull(created);
        assertNotNull(created.getId());

        created.setName("Edited Group");
        created.setDescription("Has been edited");

        var edited = restClient.put()
                .uri("/v0/group/{id}", created.getId())
                .body(created)
                .retrieve()
                .body(GroupDTO.class);
        assertNotNull(edited);
        assertEquals(created.getId(), edited.getId());
        assertEquals("Edited Group", edited.getName());
        assertEquals("Has been edited", edited.getDescription());
    }

    @Test
    void testCreateAndDelete() {
        var createDTO = new GroupCreateDTO("Delete Me", "To be deleted", false, true);

        var created = restClient.post()
                .uri("/v0/group")
                .body(createDTO)
                .retrieve()
                .body(GroupDTO.class);
        assertNotNull(created);
        assertNotNull(created.getId());

        restClient.delete()
                .uri("/v0/group/{id}", created.getId())
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(Exception.class, () ->
                restClient.delete()
                        .uri("/v0/group/{id}", UUID.randomUUID())
                        .retrieve()
                        .toBodilessEntity());
    }
}
