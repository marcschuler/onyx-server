package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A event that fires when a new client joins the server.
 * At this point, until a @{@link ClientChannelJoinEvent} fires, the client is not in any channel.
 * @param user the user data you might want to cache
 */
public record ClientServerJoinEvent(@NotNull UserSimpleDTO user) implements MessageBody {
}
