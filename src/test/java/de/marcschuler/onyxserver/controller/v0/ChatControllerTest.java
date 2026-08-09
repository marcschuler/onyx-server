package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.MessageDTO;
import de.marcschuler.onyxserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.onyxserver.dto.data.message.MessageCreationDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.chat.ChatMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
@Slf4j
class ChatControllerTest {

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
    void testMessages() {
        var messages = restClient.get()
                .uri("/v0/chat/{id}/messages?page=0&size=10", TestService.CHAT_LOBBY_ID)
                .retrieve()
                .body(String.class);
        assertNotNull(messages);
    }

    @Test
    void testMessagesLatest() {
        var messages = restClient.get()
                .uri("/v0/chat/{id}/messages/latest?size=10", TestService.CHAT_LOBBY_ID)
                .retrieve()
                .body(String.class);
        assertNotNull(messages);
    }

    @Test
    void testSendMessage() {
        var markdown = new MarkdownMessageContentDTO();
        markdown.setText("Hello from test!");

        var creationDTO = new MessageCreationDTO();
        creationDTO.setContent(List.of(markdown));

        var message = restClient.post()
                .uri("/v0/chat/{id}/message", TestService.CHAT_LOBBY_ID)
                .body(creationDTO)
                .retrieve()
                .body(String.class);
        assertNotNull(message);

        verify(webSocketConnectionService).send(isA(List.class), isA(ChatMessageEvent.class));
    }

    @Test
    void testDeleteMessage() {
        var markdown = new MarkdownMessageContentDTO();
        markdown.setText("Hello from delete test!");

        var creationDTO = new MessageCreationDTO();
        creationDTO.setContent(List.of(markdown));

        var created = restClient.post()
                .uri("/v0/chat/{id}/message", TestService.CHAT_LOBBY_ID)
                .body(creationDTO)
                .retrieve()
                .body(MessageDTO.class);
        assertNotNull(created);
        assertNotNull(created.getId());

        assertTrue(containsMessage(created.getId()));

        restClient.delete()
                .uri("/v0/chat/{id}/message/{messageId}", TestService.CHAT_LOBBY_ID, created.getId())
                .retrieve()
                .toBodilessEntity();

        assertFalse(containsMessage(created.getId()));
    }

    private boolean containsMessage(UUID messageId) {
        var messages = restClient.get()
                .uri("/v0/chat/{id}/messages?page=0&size=100", TestService.CHAT_LOBBY_ID)
                .retrieve()
                .body(JsonNode.class);
        if (messages == null || !messages.has("content")) {
            return false;
        }
        for (JsonNode message : messages.get("content")) {
            if (messageId.toString().equals(message.get("id").asText())) {
                return true;
            }
        }
        return false;
    }
}
