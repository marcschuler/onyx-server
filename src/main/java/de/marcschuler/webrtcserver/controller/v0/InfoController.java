package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.ServerDTO;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v0/info")
@RequiredArgsConstructor
public class InfoController {

    private final ServerService serverService;

    @GetMapping("server")
    public List<ServerDTO> info() {
        return serverService.info();
    }

}
