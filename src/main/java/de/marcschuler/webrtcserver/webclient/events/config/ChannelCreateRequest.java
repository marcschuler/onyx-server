package de.marcschuler.webrtcserver.webclient.events.config;

import de.marcschuler.webrtcserver.webclient.events.EventBody;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelCreateRequest extends EventBody {
    private String name;
    private UUID section;;
}
