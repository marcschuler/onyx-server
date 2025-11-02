package de.marcschuler.webrtcserver.webclient.messages.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeerOfferForward extends MessageBody {
    private String clientFrom;
    private JsonNode offer; //TODO specify what kind of data is expected
}
