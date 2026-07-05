package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerAnswer extends MessageBody {
    private String clientTo;

    private JsonNode answer;
}
