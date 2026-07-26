package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.dto.ChannelCreateDTO;
import de.marcschuler.onyxserver.dto.data.ChannelDTO;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.ChannelService;
import de.marcschuler.onyxserver.service.PermissionService;
import de.marcschuler.onyxserver.service.SectionService;
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

    private final PermissionService permissionService;

    private final ServerMapper serverMapper;

    @GetMapping("{id}")
    public ChannelDTO channel(@PathVariable UUID id) {
        return channelService.get(id).map(serverMapper::mapToDTO)
                .orElseThrow();
    }

    @PostMapping
    public ChannelDTO create(@RequestBody ChannelCreateDTO channelCreateDTO) {
        var section = sectionService.get(channelCreateDTO.getSectionId()).orElseThrow();
        permissionService.checkControllerAccess(section, null, PermissionType.SECTION_CHANNEL_CREATE);

        var channel = channelService.create(channelCreateDTO.getName(), section);
        return serverMapper.mapToDTO(channel);
    }

    @PutMapping("{id}")
    public ChannelDTO edit(@PathVariable UUID id, @RequestBody ChannelDTO channelDTO) {
        var channel = channelService.get(id).orElseThrow();

        permissionService.checkControllerAccess(channel, PermissionType.CHANNEL_EDIT);

        channelService.edit(channel, channelDTO);
        return serverMapper.mapToDTO(channel);
    }

    @Override
    public void order(UUID id, int newOrder) {
        var channel = channelService.get(id).orElseThrow();

        permissionService.checkControllerAccess(channel, PermissionType.SECTION_CHANNEL_ORDER);

        channelService.order(channel, newOrder);
    }

    @PutMapping("{id}/move/{newParentId}/{newOrder}")
    public void move(@PathVariable UUID id, @PathVariable UUID newParentId, @PathVariable int newOrder) {
        var channel = channelService.get(id).orElseThrow();
        var newSection = sectionService.get(newParentId).orElseThrow();

        // Check both old and new section for move permissions
        permissionService.checkControllerAccess(channel.getSection(),channel, PermissionType.SECTION_CHANNEL_MOVE);
        permissionService.checkControllerAccess(newSection, channel,PermissionType.SECTION_CHANNEL_MOVE);

        permissionService.checkControllerAccess(channel, PermissionType.CHANNEL_EDIT);

        channelService.move(channel, newSection, newOrder);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        var channel = channelService.get(id).orElseThrow();

        permissionService.checkControllerAccess(channel, PermissionType.CHANNEL_DELETE);

        channelService.delete(channel);
    }
}
