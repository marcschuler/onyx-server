package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ChannelCreateDTO;
import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.dto.data.ChannelWriteDTO;
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
public class ChannelController implements OrderableController {

    private final SectionService sectionService;
    private final ChannelService channelService;

    private final ServerMapper serverMapper;

    @PostMapping
    public ChannelDTO create(@RequestBody ChannelCreateDTO channelDTO) {
        var section = sectionService.get(channelDTO.getSectionId()).orElseThrow();
        var channel = channelService.create(channelDTO.getName(), section);
        return serverMapper.mapToDTO(channel);
    }

    @PutMapping("{id}")
    public ChannelDTO edit(@PathVariable UUID id, @RequestBody ChannelWriteDTO channelDTO) {
        var channel = channelService.get(id).orElseThrow();
        channelService.edit(channel, channelDTO);
        return serverMapper.mapToDTO(channel);
    }

    @Override
    public void order(UUID id, int newOrder) {
        var channel = channelService.get(id).orElseThrow();
        channelService.order(channel, newOrder);
    }

    @PutMapping("{id}/move/{newParentId}/{newOrder}")
    public void move(@PathVariable UUID id, @PathVariable UUID newParentId, @PathVariable int newOrder) {
        var channel = channelService.get(id).orElseThrow();
        var newSection = sectionService.get(newParentId).orElseThrow();
        channelService.move(channel, newSection, newOrder);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        var channel = channelService.get(id).orElseThrow();
        channelService.delete(channel);
    }
}
