package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.FileDTO;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
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

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

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

    @Test
    void testEdit() {
        var dto = new ServerDTO();
        dto.setId(TestService.SERVER_ID);
        dto.setName("Updated Server");
        dto.setDescription(List.of());

        var updated = restClient.put()
                .uri("/v0/server/{id}", TestService.SERVER_ID)
                .body(dto)
                .retrieve()
                .body(ServerDTO.class);

        assertNotNull(updated);
        assertEquals("Updated Server", updated.getName());
    }

    @Test
    void testIconUploadAndDelete() {
        var parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new ByteArrayResource("fake-icon-data".getBytes()) {
            @Override
            public String getFilename() {
                return "icon.png";
            }
        });

        var file = restClient.post()
                .uri("/v0/server/{id}/icon", TestService.SERVER_ID)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .body(FileDTO.class);

        assertNotNull(file);
        assertEquals("icon.png", file.getFilename());

        restClient.delete()
                .uri("/v0/server/{id}/icon", TestService.SERVER_ID)
                .retrieve()
                .toBodilessEntity();
    }
}
