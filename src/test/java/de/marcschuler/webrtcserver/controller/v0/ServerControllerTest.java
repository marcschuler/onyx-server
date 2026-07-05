package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Slf4j
class ServerControllerTest {

    @Autowired
    private ServerController serverController;
    @Autowired
    private ServerService serverService;
    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.setSecurityContext(testService.userAdmin());
    }

    @AfterEach
    void tearDown() {
        testService.resetSecurityContext();
    }

    @Test
    @Disabled("contentResponse id is null on creation")
    void testDescriptions() {
        var serverId = serverService.defaultServer().getId();

        var server = serverController.get(serverId);
        assertEquals(1, server.getDescription().size());

        server.setDescription(List.of());
        server = serverController.edit(server.getId(),server);
        assertEquals(0, server.getDescription().size());

        server = serverController.get(serverId);
        assertEquals(0, server.getDescription().size());

        var content = new MarkdownMessageContentDTO();
        content.setText("example");
        server.setDescription(List.of(content));
        server = serverController.edit(server.getId(),server);
        assertNotNull(server.getDescription());
        assertEquals(1,server.getDescription().size());
        assertInstanceOf(MarkdownMessageContentDTO.class, server.getDescription().getFirst());
        assertEquals("example", ((MarkdownMessageContentDTO) server.getDescription().getFirst()).getText());

        ((MarkdownMessageContentDTO) server.getDescription().getFirst()).setText("kohlekohlekohle");
        server = serverController.edit(server.getId(),server);
        assertInstanceOf(MarkdownMessageContentDTO.class, server.getDescription().getFirst());
        assertEquals("kohlekohlekohle", ((MarkdownMessageContentDTO) server.getDescription().getFirst()).getText());
    }


}