package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.ChannelReference;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/server/{serverId}/sectio")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    private final ServerMapper serverMapper;

    @PostMapping
    public ChannelReference create(@RequestBody ChannelReference channelReference) {
        var channel = channelService.create(serverMapper.mapFromDTO(channelReference));
        return serverMapper.mapToDTO(channel);
    }
}
