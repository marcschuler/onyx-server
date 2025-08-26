package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.dto.ServerInfo;
import de.marcschuler.webrtcserver.service.ServerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0")
@RequiredArgsConstructor
public class ServerController {

    private final ServerInfoService serverInfoService;

    @GetMapping("info/server")
    public ServerInfo serverInfo() {
        return serverInfoService.serverInfo();
    }

}
