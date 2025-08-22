package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0")
public class ServerController {

    private Server server;

    @GetMapping
    public Server server(){
        return this.server;
    }
}
