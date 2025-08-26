package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

@Data
public class ChannelChangeRequest extends EventBody {
    private String channelId;
}
