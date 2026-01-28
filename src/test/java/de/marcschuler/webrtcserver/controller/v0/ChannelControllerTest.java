package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ChannelCreateDTO;
import de.marcschuler.webrtcserver.service.ServerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class ChannelControllerTest {

    @Autowired
    private ChannelController channelController;
    @Autowired
    private ServerService serverService;

    @Test
    void testCreate(){
        var section = serverService.defaultServer().getSections().getFirst();
        assertEquals(1,section.getChannels().size());
        log.info("Create channel in section {}", section.getId());
        var channel = channelController.create(new ChannelCreateDTO(section.getId(),0));
        section = serverService.defaultServer().getSections().getFirst();
        log.info("channel: {}", channel);
        log.info("section: {}", section);
        assertNotNull(channel);
        assertNotNull(channel.getId());
        assertEquals(section.getChannels().getLast().getId(),channel.getId());
        assertEquals(2,section.getChannels().size());
    }

    @Test
    void testCreateDelete(){
        var section = serverService.defaultServer().getSections().getFirst();
        var channel = channelController.create(new ChannelCreateDTO(section.getId(),0));
        assertEquals(section.getChannels().getLast().getId(),channel.getId());

    }

}