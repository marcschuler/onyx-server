package de.marcschuler.webrtcserver.webclient.events;

import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.webclient.WebClient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServerInfoEventBody extends EventBody {
    private Server server;
    private List<WebClient> clients;
}
