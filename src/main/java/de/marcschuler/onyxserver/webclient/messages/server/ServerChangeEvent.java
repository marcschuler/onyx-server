package de.marcschuler.onyxserver.webclient.messages.server;

import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * An event that fires every time the server changes, e.g. the server name or it's descriptions
 * @param server the server data
 */
public record ServerChangeEvent(@NotNull ServerDTO server) implements MessageBody {
}
