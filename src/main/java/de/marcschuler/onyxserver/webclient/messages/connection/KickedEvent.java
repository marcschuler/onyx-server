package de.marcschuler.onyxserver.webclient.messages.connection;

import de.marcschuler.onyxserver.webclient.KickReason;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * This event fires when you have been kicked from the server.
 * It's analogous to the @{@link ClientKickEvent} expect only the user that
 * has been fired get's this event and the message may be different.
 * @param reason the reason for the kick
 * @param message a admin-defined message related to the kick. might be null
 */
public record KickedEvent(@NotNull KickReason reason, String message) implements MessageBody {
}
