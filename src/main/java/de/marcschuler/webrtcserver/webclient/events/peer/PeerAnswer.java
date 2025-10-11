package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.MessageBody;
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
