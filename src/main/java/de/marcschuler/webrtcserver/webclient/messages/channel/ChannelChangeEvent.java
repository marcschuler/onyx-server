package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Indicates that any property of a channel has been changed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelChangeEvent extends MessageBody {
    @NotNull
    private ChannelDTO channel;

}
