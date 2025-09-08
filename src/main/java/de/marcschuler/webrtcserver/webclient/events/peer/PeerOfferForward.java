package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PeerOfferForward extends EventBody {
    private String clientFrom;
    private JsonNode offer; //TODO specify what kind of data is expected
}
