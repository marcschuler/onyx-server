package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.ServerWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/server/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    private final ServerMapper serverMapper;

    @PutMapping("{serverId}")
    public ServerDTO edit(@PathVariable UUID serverId, @RequestBody ServerWriteDTO serverDto) {
        var server = serverService.get(serverId).orElseThrow();
        serverMapper.update(server, serverDto);
        serverService.save(server);
        return serverMapper.mapToDTO(server);
    }

    @GetMapping("{id}")
    public ServerDTO get(@PathVariable UUID id) {
        ServerDTO serverDTO = serverMapper.mapToDTO(serverService.get(id).orElseThrow());
        return serverDTO;
    }


}
