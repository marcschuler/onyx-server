package de.marcschuler.webrtcserver.webclient.messages.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.Data;

@Data
public class PeerOffer extends MessageBody {
    private String clientTo;

    private JsonNode offer;
}
