package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.ChannelDTO;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelChangeEvent;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelCreateEvent;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelDeleteEvent;
import de.marcschuler.onyxserver.webclient.messages.channel.ChannelMoveEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@OnyxTest
@Transactional
class ChannelServiceTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private TestService testService;

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

    @Test
    void testCreate() {
        var section = testService.sectionTalk();
        var initialSize = section.getChannels().size();

        var channel = channelService.create("New Channel", section);

        assertNotNull(channel.getId());
        assertEquals("New Channel", channel.getName());
        assertEquals(initialSize + 1, section.getChannels().size());
        verify(webSocketConnectionService).sendToAll(isA(ChannelCreateEvent.class));
    }

    @Test
    void testGet() {
        var channel = testService.channelLobby();
        var found = channelService.get(channel.getId());

        assertTrue(found.isPresent());
        assertEquals(channel.getId(), found.get().getId());
    }

    @Test
    void testGetNotFound() {
        var found = channelService.get(java.util.UUID.randomUUID());
        assertTrue(found.isEmpty());
    }

    @Test
    void testOrder() {
        var section = testService.sectionTalk();
        var channel = section.getChannels().getFirst();

        channelService.order(channel, 1);

        assertEquals(1, section.getChannels().indexOf(channel));
        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }

    @Test
    void testMoveToOtherSection() {
        var talkSection = testService.sectionTalk();
        var lobbySection = testService.sectionLobby();
        var channel = talkSection.getChannels().getFirst();

        channelService.move(channel, lobbySection, 0);

        assertFalse(talkSection.getChannels().contains(channel));
        assertTrue(lobbySection.getChannels().contains(channel));
        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }

    @Test
    void testMoveToSameSectionDoesNothing() {
        var section = testService.sectionTalk();
        var channel = section.getChannels().getFirst();

        channelService.move(channel, section, 0);

        assertTrue(section.getChannels().contains(channel));
        verify(webSocketConnectionService, org.mockito.Mockito.never()).sendToAll(isA(ChannelMoveEvent.class));
    }

    @Test
    void testEdit() {
        var channel = testService.channelLobby();
        var dto = new ChannelDTO();
        dto.setName("Renamed Lobby");

        channelService.edit(channel, dto);

        assertEquals("Renamed Lobby", channel.getName());
        verify(webSocketConnectionService).sendToAll(isA(ChannelChangeEvent.class));
    }

    @Test
    void testDelete() {
        var channel = testService.channelLobby();
        var section = channel.getSection();

        channelService.delete(channel);

        assertFalse(section.getChannels().contains(channel));
        verify(webSocketConnectionService).sendToAll(isA(ChannelDeleteEvent.class));
    }
}
