package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class ChannelMoveEvent extends MessageBody {
    @NotNull
    private UUID channelId;

    @NotNull
    private int order;
    private UUID sectionId;
}
