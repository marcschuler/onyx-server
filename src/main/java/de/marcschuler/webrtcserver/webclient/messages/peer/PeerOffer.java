package de.marcschuler.webrtcserver.webclient.messages.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PeerOffer extends MessageBody {
    @NotNull
    private String clientTo;
    @NotNull
    private JsonNode offer;
}
