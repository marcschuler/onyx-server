package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.Data;

import java.util.UUID;

/**
 * A request from a user to change the channel.
 * The server may not allow this
 */
@Data
public class ClientChannelChangeRequest extends MessageBody {

    private UUID channelId;

}
