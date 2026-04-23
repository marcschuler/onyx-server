package de.marcschuler.webrtcserver.webclient.messages.connection;

import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fires when a client that is not you was kicked from the server
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientKickEvent extends MessageBody {

    @NotNull
    private UserSimpleDTO user;

    @NotNull
    private KickReason reason;
    private String message;
}
