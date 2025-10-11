package de.marcschuler.webrtcserver.webclient.events.client;

import de.marcschuler.webrtcserver.dto.UserReference;
import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signals that a client did leave a channel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientChannelLeaveMessage extends MessageBody {
    @NotNull
    private UserReference user;
}
