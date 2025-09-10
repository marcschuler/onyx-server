package de.marcschuler.webrtcserver.webclient.events.peer;

import com.fasterxml.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerAnswer extends EventBody {
    private String clientTo;

    private JsonNode answer;
}
