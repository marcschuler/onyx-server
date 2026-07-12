package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Slf4j
class ServerServiceTest {

    @Autowired
    private ServerService serverService;

    @Autowired
    private TestService testService;

    @Autowired
    private StorageService storageService;

    @Test
    void testDefaultServer() {
        var server = serverService.defaultServer();
        assertEquals("Onyx Server", server.getName());
        assertNotNull(server.getId());
    }

    @Test
    void testAll() {
        var servers = serverService.all();
        assertEquals(1, servers.size());
        assertEquals("Onyx Server", servers.getFirst().getName());
    }

    @Test
    void testGet() {
        var server = serverService.get(TestService.SERVER_ID);
        assertTrue(server.isPresent());
        assertEquals("Onyx Server", server.get().getName());
    }

    @Test
    void testGetNotFound() {
        var server = serverService.get(UUID.randomUUID());
        assertTrue(server.isEmpty());
    }

    @Test
    @Transactional
    void testUpdateName() {
        var server = serverService.get(TestService.SERVER_ID).orElseThrow();
        var dto = new ServerDTO();
        dto.setId(server.getId());
        dto.setName("Updated Server");

        var updated = serverService.update(server, dto);
        assertEquals("Updated Server", updated.getName());

        var reloaded = serverService.get(TestService.SERVER_ID).orElseThrow();
        assertEquals("Updated Server", reloaded.getName());
    }

    @Test
    @Transactional
    void testUpdateDescription() {
        var server = serverService.get(TestService.SERVER_ID).orElseThrow();
        var dto = new ServerDTO();
        dto.setId(server.getId());
        dto.setName(server.getName());

        var desc = new MarkdownMessageContentDTO();
        desc.setText("New description");
        dto.setDescription(List.of(desc));

        var updated = serverService.update(server, dto);
        assertNotNull(updated.getDescription());
        assertEquals(1, updated.getDescription().size());
        assertEquals("New description", ((MarkdownMessageContent) updated.getDescription().get(0)).getText());
    }

    @Test
    @Transactional
    void testUpdateDescriptionNull() {
        var server = serverService.get(TestService.SERVER_ID).orElseThrow();
        var dto = new ServerDTO();
        dto.setId(server.getId());
        dto.setName(server.getName());
        dto.setDescription(null);

        var updated = serverService.update(server, dto);
        assertNull(updated.getDescription());
    }

    @Test
    @Transactional
    void testSave() {
        var server = serverService.get(TestService.SERVER_ID).orElseThrow();
        server.setName("Saved Server");
        serverService.save(server);

        var reloaded = serverService.get(TestService.SERVER_ID).orElseThrow();
        assertEquals("Saved Server", reloaded.getName());
    }

    @Test
    @Transactional
    void testSetIcon() throws Exception {
        var server = serverService.get(TestService.SERVER_ID).orElseThrow();
        assertNull(server.getIcon());

        var multipartFile = new MockMultipartFile(
                "file", "icon.png", "image/png",
                new ByteArrayInputStream("fake-icon-data".getBytes()));
        var file = storageService.uploadFile(multipartFile);

        serverService.setIcon(server, file);

        var reloaded = serverService.get(TestService.SERVER_ID).orElseThrow();
        assertNotNull(reloaded.getIcon());
        assertEquals(file.getId(), reloaded.getIcon().getId());
    }

}
