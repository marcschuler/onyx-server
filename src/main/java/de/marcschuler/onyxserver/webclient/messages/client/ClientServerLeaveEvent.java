package de.marcschuler.onyxserver.webclient.messages.client;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * This event fires when a user leaves the server gracefully
 * @param userId the id of the user
 */
public record ClientServerLeaveEvent(@NotNull String userId) implements MessageBody {
}
