package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v0/info")
@RequiredArgsConstructor
public class InfoController {

    private final ServerService serverService;

    private final ServerMapper serverMapper;

    @GetMapping("server/default")
    private ServerDTO defaultServerInfo() {
        return serverMapper.mapToDTO(serverService.defaultServer());
    }

    @GetMapping("server")
    public List<ServerDTO> info() {
        return serverService.all()
                .stream()
                .map(serverMapper::mapToDTO)
                .toList();
    }

    @GetMapping("server/{id}")
    public ServerDTO info(@PathVariable UUID id) {
        return serverMapper.mapToDTO(serverService.get(id).orElseThrow());
    }

}
