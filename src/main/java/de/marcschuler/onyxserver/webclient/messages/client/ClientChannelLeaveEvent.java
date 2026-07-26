package de.marcschuler.onyxserver.webclient.messages.client;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * This event fires when a client leaves a channel
 * @param userId the id of the user
 */
public record ClientChannelLeaveEvent(@NotNull String userId) implements MessageBody {
}
