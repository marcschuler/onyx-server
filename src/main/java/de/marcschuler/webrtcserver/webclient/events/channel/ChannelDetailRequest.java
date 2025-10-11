package de.marcschuler.webrtcserver.webclient.events.channel;

import de.marcschuler.webrtcserver.webclient.events.MessageBodyRequest;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDetailRequest extends MessageBodyRequest {
    private UUID channelId;
}
