package de.marcschuler.webrtcserver.webclient.events.channel;

import de.marcschuler.webrtcserver.webclient.events.EventBodyRequest;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDetailRequest extends EventBodyRequest {
    private UUID channelId;
}
