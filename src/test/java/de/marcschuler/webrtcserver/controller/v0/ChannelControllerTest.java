package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.dto.data.ChannelCreateDTO;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.ServerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Slf4j
class ChannelControllerTest {

    @Autowired
    private ChannelController channelController;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCreate(){
        var section = serverService.defaultServer().getSections().getFirst();
        assertEquals(1,section.getChannels().size());
        log.info("Create channel in section {}", section.getId());
        var createDTO  =new ChannelCreateDTO(section.getId(),0);
        createDTO.setName("New Channel Name");
        var channel = channelController.create(createDTO);

        section = sectionService.get(section.getId()).orElseThrow();
        log.info("channel: {}", mapper.writeValueAsString(channel));
        log.info("section: {}", mapper.writeValueAsString(section));

        assertNotNull(channel);
        assertNotNull(channel.getId());
        assertEquals("New Channel Name", channel.getName());

        assertEquals(2,section.getChannels().size());
        assertEquals(channel.getId(),section.getChannels().getLast().getId());
    }

    @Test
    void testCreateDelete(){
        var section = serverService.defaultServer().getSections().getFirst();
        var channel = channelController.create(new ChannelCreateDTO(section.getId(),0));
        assertEquals(section.getChannels().getLast().getId(),channel.getId());

    }

}