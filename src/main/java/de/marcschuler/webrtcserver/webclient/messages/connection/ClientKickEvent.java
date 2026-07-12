package de.marcschuler.webrtcserver.webclient.messages.connection;

import de.marcschuler.webrtcserver.webclient.KickReason;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * Fires when a client is kicked from the server.
 * Should internally handled the same as a @{@link de.marcschuler.webrtcserver.webclient.messages.client.ClientServerLeaveEvent}
 * @param userId the user that has been kicked
 * @param reason the reason for the kick
 * @param message a admin-defined message related to the kick. might be null
 */
public record ClientKickEvent(@NotNull String userId, @NotNull KickReason reason,
                              String message) implements MessageBody {

}
