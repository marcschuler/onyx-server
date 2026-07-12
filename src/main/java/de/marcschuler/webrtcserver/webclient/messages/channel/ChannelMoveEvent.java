package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * The event fires when a channel is moved
 * @param channelId the id of the channel
 * @param order the order of the channel within a section
 * @param sectionId the id of the section if the section has been changed
 */
public record ChannelMoveEvent(@NotNull UUID channelId, @NotNull int order, UUID sectionId) implements MessageBody {
}
