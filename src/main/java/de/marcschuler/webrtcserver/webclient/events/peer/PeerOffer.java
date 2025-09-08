package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.Data;

import java.util.Map;

@Data
public class PeerOffer extends EventBody {
    private String clientTo;

    private JsonNode offer;
}
