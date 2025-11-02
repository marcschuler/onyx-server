package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBodyRequest;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDetailRequest extends MessageBodyRequest {
    private UUID channelId;
}
