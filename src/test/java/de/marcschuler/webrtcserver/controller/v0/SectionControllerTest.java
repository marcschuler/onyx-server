package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.dto.SectionCreateDTO;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionChangeEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionCreateEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionDeleteEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionMoveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
class SectionControllerTest {

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
        var response = restClient.get()
                .uri("/v0/section/{id}", TestService.SECTION_LOBBY_ID)
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Lobby"));
    }

    @Test
    void testGetNotFound() {
        assertThrows(Exception.class, () ->
                restClient.get()
                        .uri("/v0/section/{id}", UUID.randomUUID())
                        .retrieve()
                        .toBodilessEntity());
    }

    @Test
    void testCreate() {
        var createDTO = new SectionCreateDTO("New Section");

        var response = restClient.post()
                .uri("/v0/section")
                .body(createDTO)
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("New Section"));

        verify(webSocketConnectionService).sendToAll(isA(SectionCreateEvent.class));
    }

    @Test
    void testEdit() {
        var sectionDto = new de.marcschuler.webrtcserver.dto.data.SectionDTO();
        sectionDto.setId(TestService.SECTION_LOBBY_ID);
        sectionDto.setName("Updated Lobby");
        sectionDto.setChannels(java.util.List.of());

        var response = restClient.put()
                .uri("/v0/section/{sectionId}", TestService.SECTION_LOBBY_ID)
                .body(sectionDto)
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Updated Lobby"));

        verify(webSocketConnectionService).sendToAll(isA(SectionChangeEvent.class));
    }

    @Test
    void testOrder() {
        var response = restClient.put()
                .uri("/v0/section/{id}/order/{newOrder}", TestService.SECTION_LOBBY_ID, 2)
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(webSocketConnectionService).sendToAll(isA(SectionMoveEvent.class));
    }

    @Test
    void testDelete() {
        var response = restClient.delete()
                .uri("/v0/section/{sectionId}", TestService.SECTION_CHAT_ID)
                .retrieve()
                .toBodilessEntity();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(webSocketConnectionService).sendToAll(isA(SectionDeleteEvent.class));
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(Exception.class, () ->
                restClient.delete()
                        .uri("/v0/section/{sectionId}", UUID.randomUUID())
                        .retrieve()
                        .toBodilessEntity());
    }
}
