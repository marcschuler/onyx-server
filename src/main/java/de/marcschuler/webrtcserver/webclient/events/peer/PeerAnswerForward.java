package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeerAnswerForward extends MessageBody {
    private String clientFrom;
    private JsonNode answer;
}
