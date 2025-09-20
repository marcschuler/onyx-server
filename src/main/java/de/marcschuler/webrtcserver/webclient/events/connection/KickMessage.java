package de.marcschuler.webrtcserver.webclient.events.connection;

import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickMessage extends EventBody {
    private KickReason reason;
}
