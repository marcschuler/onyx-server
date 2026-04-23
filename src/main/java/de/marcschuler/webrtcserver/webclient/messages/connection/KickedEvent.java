package de.marcschuler.webrtcserver.webclient.messages.connection;

import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    Fires when you have been kicked from the server
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickedEvent extends MessageBody {
    @NotNull
    private KickReason reason;
    private String message;
}
