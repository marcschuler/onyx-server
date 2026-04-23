package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDeleteEvent extends MessageBody {
    @NotNull
    private UUID channelId;
}
