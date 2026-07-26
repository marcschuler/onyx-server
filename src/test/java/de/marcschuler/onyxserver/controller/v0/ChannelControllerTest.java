package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.ChannelCreateDTO;
import de.marcschuler.onyxserver.dto.data.ChannelDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelCreateEvent;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelDeleteEvent;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelMoveEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
@Slf4j
@SuppressWarnings("LoggingSimilarMessage")
class ChannelControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;
    @Autowired
    private ObjectMapper mapper;

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
    void testCreate() {
        var section = testService.sectionLobby();
        assertEquals(1, section.getChannels().size());

        var createDTO = new ChannelCreateDTO();
        createDTO.setSectionId(section.getId());
        createDTO.setName("New Channel Name");

        var channel = restClient.post()
                .uri("/v0/channel")
                .body(createDTO)
                .retrieve()
                .body(ChannelDTO.class);
        assertNotNull(channel);
        assertNotNull(channel.getId());
        assertEquals("New Channel Name", channel.getName());

        verify(webSocketConnectionService).sendToAll(isA(ChannelCreateEvent.class));
    }

    @Test
    void testCreateDelete() {
        var section = testService.sectionTalk();
        assertEquals(2, section.getChannels().size());

        var createDTO = new ChannelCreateDTO();
        createDTO.setSectionId(section.getId());
        createDTO.setName("New Channel Name");

        var channel = restClient.post()
                .uri("/v0/channel")
                .body(createDTO)
                .retrieve()
                .body(ChannelDTO.class);
        assertNotNull(channel);
        assertNotNull(channel.getId());

        restClient.delete()
                .uri("/v0/channel/{id}", channel.getId())
                .retrieve()
                .toBodilessEntity();

        verify(webSocketConnectionService).sendToAll(isA(ChannelDeleteEvent.class));
    }

    @Test
    void testMoveInSection() {
        var section = testService.sectionTalk();
        assertEquals(2, section.getChannels().size());

        var channel = section.getChannels().getFirst();
        restClient.put()
                .uri("/v0/channel/{id}/order/{newOrder}", channel.getId(), 1)
                .retrieve()
                .toBodilessEntity();

        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }

    @Test
    void testMoveToOtherSection() {
        var section = testService.sectionTalk();
        var newSection = testService.sectionLobby();
        assertEquals(2, section.getChannels().size());
        assertEquals(1, newSection.getChannels().size());

        var channel = section.getChannels().getFirst();
        restClient.put()
                .uri("/v0/channel/{id}/move/{newParentId}/{newOrder}", channel.getId(), newSection.getId(), 0)
                .retrieve()
                .toBodilessEntity();

        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }
}
