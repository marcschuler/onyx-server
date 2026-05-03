package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * A request from a user to change the channel.
 * The server may not allow this.
 * If the server allows this, a ClientChannelJoinMessage is send
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientChannelJoinRequest extends MessageBody {

    @NotNull
    private UUID channelId;

}
