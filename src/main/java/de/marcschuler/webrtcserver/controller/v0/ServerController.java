package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.ServerWritableDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v0/server/")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    private final ServerMapper serverMapper;

    @PutMapping("{sectionId}")
    public ServerDTO edit(@RequestBody ServerWritableDTO serverDto, @PathVariable UUID serverId) {
        var server = serverService.get(serverId).orElseThrow();
        serverMapper.update(server,serverDto);
        serverService.save(server);
        return serverMapper.mapToDTO(server);
    }
}
