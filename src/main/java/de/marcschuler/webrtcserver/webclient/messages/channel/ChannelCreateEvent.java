package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * A event that fires when a new channel has been created
 * @param sectionId the id of the section the channel is in
 * @param order the order of the channel within the section
 * @param channel the channel data
 */
public record ChannelCreateEvent(@NotNull UUID sectionId, @NotNull int order,
                                 @NotNull ChannelDTO channel) implements MessageBody {
}
