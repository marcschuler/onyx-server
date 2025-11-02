package de.marcschuler.webrtcserver.webclient.messages.config;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelCreateRequest extends MessageBody {
    private String name;
    private UUID section;;
}
