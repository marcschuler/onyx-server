package de.marcschuler.webrtcserver.webclient.messages.peer;

import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerAnswer extends MessageBody {
    private String clientTo;

    private JsonNode answer;
}
