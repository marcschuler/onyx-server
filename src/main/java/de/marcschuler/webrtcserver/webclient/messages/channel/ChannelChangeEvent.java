package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * An event that fires if a channel did change
 * @param channel the modified channel
 */
public record ChannelChangeEvent(@NotNull ChannelDTO channel) implements MessageBody {
}
