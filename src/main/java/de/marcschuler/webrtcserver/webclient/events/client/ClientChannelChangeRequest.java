package de.marcschuler.webrtcserver.webclient.events.client;

import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.Data;

import java.util.UUID;

/**
 * A request from a user to change the channel.
 * The server may not allow this
 */
@Data
public class ClientChannelChangeRequest extends EventBody {

    private UUID channelId;

}
