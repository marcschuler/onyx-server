package de.marcschuler.webrtcserver.webclient.events.peer;

import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.Data;

import java.util.List;

@Data
public class IceServerData extends EventBody {
    private List<IceServer> iceServers;

    @Data
    public static class IceServer {
        private String urls;
        private String username;
        private String credential;
    }
}
