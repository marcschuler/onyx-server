package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

import java.util.Map;

@Data
public class ClientPeerOffer extends EventBody {
    private String clientTo;

    private Map<String,Object> offer;
}
