package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.dto.ChannelCreateDTO;
import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.ServerService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelCreateEvent;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelDeleteEvent;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelMoveEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@OnyxTest
@Slf4j
@SuppressWarnings("LoggingSimilarMessage")
class ChannelControllerTest {

    @Autowired
    private ChannelController channelController;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TestService testService;

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

    @Test
    void testCreate(){
        var section = testService.sectionLobby();
        assertEquals(1,section.getChannels().size());

        log.info("Create channel in section {}", section.getId());
        var createDTO  =new ChannelCreateDTO();
        createDTO.setSectionId(section.getId());
        createDTO.setName("New Channel Name");
        var channel = channelController.create(createDTO);

        section =  testService.sectionLobby();
        log.info("channel: {}", mapper.writeValueAsString(channel));
        log.info("section: {}", mapper.writeValueAsString(section));

        assertNotNull(channel);
        assertNotNull(channel.getId());
        assertEquals("New Channel Name", channel.getName());

        assertEquals(2,section.getChannels().size());
        assertEquals(channel.getId(),section.getChannels().getLast().getId());

        verify(webSocketConnectionService).sendToAll(isA(ChannelCreateEvent.class));
    }

    @Test
    void testCreateDelete(){
        var section = testService.sectionTalk();
        assertEquals(2,section.getChannels().size());
        var dto = new ChannelCreateDTO();
        dto.setSectionId(section.getId());
        dto.setName("New Channel Name");
        var channel = channelController.create(dto);

        section = testService.sectionTalk();
        assertEquals(3,section.getChannels().size());

        assertEquals(section.getChannels().getLast().getId(),channel.getId());

        channelController.delete(channel.getId());
        section = testService.sectionTalk();
        assertEquals(2,section.getChannels().size());
        assertNotEquals(channel.getId(),section.getChannels().getFirst().getId());

        verify(webSocketConnectionService).sendToAll(isA(ChannelDeleteEvent.class));
    }

    @Test
    void testMoveInSection(){
        var section = testService.sectionTalk();
        assertEquals(2,section.getChannels().size());


        var channel = section.getChannels().getFirst();
        channelController.order(channel.getId(),1);

        section = testService.sectionTalk();
        assertEquals(2,section.getChannels().size());
        assertEquals(channel.getId(),section.getChannels().getLast().getId());

        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }

    @Test
    void testMoveToOtherSection(){
        var section = testService.sectionTalk();
        var newSection = testService.sectionLobby();
        assertEquals(2,section.getChannels().size());
        assertEquals(1,newSection.getChannels().size());


        var channel = section.getChannels().getFirst();
        channelController.move(channel.getId(),newSection.getId(),0);

        section = testService.sectionTalk();
        newSection = testService.sectionLobby();

        log.info("channel: {}", mapper.writeValueAsString(channel));
        log.info("newSection: {}", mapper.writeValueAsString(newSection));
        log.info("section: {}", mapper.writeValueAsString(section));

        assertEquals(1,section.getChannels().size());
        assertEquals(2,newSection.getChannels().size());

        assertEquals(channel.getId(),newSection.getChannels().getFirst().getId());
        verify(webSocketConnectionService).sendToAll(isA(ChannelMoveEvent.class));
    }

}