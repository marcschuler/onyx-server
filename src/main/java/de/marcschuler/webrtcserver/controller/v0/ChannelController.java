package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/channel",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChannelController implements OrderableController{

    private final SectionService sectionService;
    private final ChannelService channelService;

    private final ServerMapper serverMapper;

    @PostMapping
    public ChannelDTO create(@RequestBody ChannelDTO channelDTO) {
        var channel = channelService.create(serverMapper.mapFromDTO(channelDTO));
        return serverMapper.mapToDTO(channel);
    }

    @Override
    public void order(UUID id, int newOrder) {
        var channel = channelService.get(id).orElseThrow();
        var section = channel.getSection();
        channelService.reorder(channel,newOrder);
    }

    @Override
    public void move(UUID id, UUID newParentId, int newOrder) {
        var channel = channelService.get(id).orElseThrow();
        var section = channel.getSection();

        var newSection = sectionService.get(newParentId).orElseThrow();
        //TODO
    }
}
