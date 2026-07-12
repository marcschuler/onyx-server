package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * An event that fires when a channel is deleted.
 * As no channel data will be left only the channel id is given.
 * The server guarantees that no client is in a channel that has been deleted
 * and may send a @{@link de.marcschuler.webrtcserver.webclient.messages.client.ClientChannelLeaveEvent}
 * for every client within the channel beforehand
 * @param channelId the id of the channel
 */
public record ChannelDeleteEvent(@NotNull UUID channelId) implements MessageBody {
}
