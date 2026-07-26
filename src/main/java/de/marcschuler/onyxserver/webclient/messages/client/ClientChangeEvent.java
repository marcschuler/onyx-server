package de.marcschuler.onyxserver.webclient.messages.client;

import de.marcschuler.onyxserver.dto.data.UserSimpleDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;


/**
 * This event fires if a client has been changed, e.g. the username
 * @param user the new user data
 */
public record ClientChangeEvent(@NotNull UserSimpleDTO user) implements MessageBody {
}
