package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class ChannelMoveEvent extends MessageBody {
    private UUID channelId;

    private int order;
    private UUID sectionId;
}
