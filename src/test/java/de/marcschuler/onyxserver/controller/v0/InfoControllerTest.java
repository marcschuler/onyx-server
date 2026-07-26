package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
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
class InfoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userUser()))
                .build();
    }

    @Test
    void testAllServers() {
        var servers = restClient.get()
                .uri("/v0/info/server")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ServerDTO>>() {});
        assertNotNull(servers);
        assertFalse(servers.isEmpty());
        assertEquals("Onyx Server", servers.getFirst().getName());
    }

    @Test
    void testServerById() {
        var server = restClient.get()
                .uri("/v0/info/server/{id}", TestService.SERVER_ID)
                .retrieve()
                .body(ServerDTO.class);
        assertNotNull(server);
        assertEquals(TestService.SERVER_ID, server.getId());
        assertEquals("Onyx Server", server.getName());
    }

    @Test
    void testServerByIdNotFound() {
        var unknownId = UUID.randomUUID();
        var ex = assertThrows(Exception.class, () ->
                restClient.get()
                        .uri("/v0/info/server/{id}", unknownId)
                        .retrieve()
                        .body(ServerDTO.class));
        assertNotNull(ex);
    }
}
