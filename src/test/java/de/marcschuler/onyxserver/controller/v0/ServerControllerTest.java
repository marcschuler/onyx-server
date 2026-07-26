package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
@Slf4j
class ServerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userAdmin()))
                .build();
    }

    @Test
    void testGet() {
        var server = restClient.get()
                .uri("/v0/server/{id}", TestService.SERVER_ID)
                .retrieve()
                .body(ServerDTO.class);
        assertNotNull(server);
        assertEquals(TestService.SERVER_ID, server.getId());
        assertEquals("Onyx Server", server.getName());
        assertFalse(server.getDescription().isEmpty());
    }

    @Test
    void testGetNotFound() {
        assertThrows(Exception.class, () ->
                restClient.get()
                        .uri("/v0/server/{id}", UUID.randomUUID())
                        .retrieve()
                        .body(ServerDTO.class));
    }
}
