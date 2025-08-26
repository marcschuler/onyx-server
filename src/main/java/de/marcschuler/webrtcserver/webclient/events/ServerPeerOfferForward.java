package de.marcschuler.webrtcserver.webclient.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
@Data
@AllArgsConstructor
public class ServerPeerOfferForward extends EventBody {
    private String clientTo;
    private String clientFrom;
    private Map<String,Object> offer;
}
