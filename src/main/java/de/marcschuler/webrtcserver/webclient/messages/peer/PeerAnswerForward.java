package de.marcschuler.webrtcserver.webclient.messages.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeerAnswerForward extends MessageBody {
    private String clientFrom;
    private JsonNode answer;
}
