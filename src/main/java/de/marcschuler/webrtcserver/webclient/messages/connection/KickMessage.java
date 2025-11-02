package de.marcschuler.webrtcserver.webclient.messages.connection;

import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickMessage extends MessageBody {
    private KickReason reason;
}
