package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
public class PeerAnswerForward extends MessageBody {
    @NotNull
    private String clientFrom;
    @NotNull
    private JsonNode answer;
}
