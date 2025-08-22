package de.marcschuler.webrtcserver.webclient.events;

import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.service.WebSocketSignalingService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServerInfoEvent extends Event{
    private Server server;
    private List<WebSocketSignalingService.WebClient> clients;
}
