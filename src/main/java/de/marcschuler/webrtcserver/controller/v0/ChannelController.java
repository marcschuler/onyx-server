package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v0/server/{serverId}/channel",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    private final ServerMapper serverMapper;

    @PostMapping
    public ChannelDTO create(@RequestBody ChannelDTO channelDTO) {
        var channel = channelService.create(serverMapper.mapFromDTO(channelDTO));
        return serverMapper.mapToDTO(channel);
    }
}
