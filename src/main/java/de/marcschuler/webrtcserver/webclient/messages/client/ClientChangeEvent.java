package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;


/**
 * This event fires if a client has been changed, e.g. the username
 * @param user the new user data
 */
public record ClientChangeEvent(@NotNull UserSimpleDTO user) implements MessageBody {
}
