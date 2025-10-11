package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import lombok.Data;

@Data
public class PeerOffer extends MessageBody {
    private String clientTo;

    private JsonNode offer;
}
