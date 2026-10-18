package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.SectionCreateDTO;
import de.marcschuler.onyxserver.dto.data.SectionDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.section.SectionChangeEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionCreateEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionDeleteEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionMoveEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@OnyxTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private TestService testService;

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

    @Test
    void testFindById() {
        var section = testService.sectionLobby();
        var found = sectionService.findById(section.getId());

        assertTrue(found.isPresent());
        assertEquals(section.getId(), found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        var found = sectionService.findById(UUID.randomUUID());
        assertTrue(found.isEmpty());
    }

    @Test
    void testCreate() {
        var server = testService.sectionLobby().getServer();
        var initialSize = server.getSections().size();

        var section = sectionService.create(server, new SectionCreateDTO("New Section"));

        assertEquals("New Section", section.getName());
        assertEquals(initialSize + 1, server.getSections().size());
        assertTrue(server.getSections().stream().anyMatch(s -> "New Section".equals(s.getName())));
        verify(webSocketConnectionService).sendToAll(isA(SectionCreateEvent.class));
    }

    @Test
    void testDelete() {
        var section = testService.sectionTalk();
        var server = section.getServer();

        sectionService.delete(section);

        assertFalse(server.getSections().contains(section));
        verify(webSocketConnectionService).sendToAll(isA(SectionDeleteEvent.class));
    }

    @Test
    void testOrder() {
        var server = testService.sectionLobby().getServer();
        var section = server.getSections().getFirst();

        sectionService.order(section, 1);

        assertEquals(1, server.getSections().indexOf(section));
        verify(webSocketConnectionService).sendToAll(isA(SectionMoveEvent.class));
    }

    @Test
    void testUpdate() {
        var section = testService.sectionLobby();
        var dto = new SectionDTO();
        dto.setName("Renamed Section");

        sectionService.update(section, dto);

        assertEquals("Renamed Section", section.getName());
        verify(webSocketConnectionService).sendToAll(isA(SectionChangeEvent.class));
    }

    @Test
    void testGet() {
        var section = testService.sectionLobby();
        var found = sectionService.get(section.getId());

        assertTrue(found.isPresent());
        assertEquals(section.getId(), found.get().getId());
    }
}
