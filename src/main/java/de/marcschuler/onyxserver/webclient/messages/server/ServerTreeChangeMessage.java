package de.marcschuler.onyxserver.webclient.messages.server;

import de.marcschuler.onyxserver.dto.data.SectionExtendedDTO;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.dto.data.UserSimpleDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Sends the whole section tree (sections, channels, clients)
 * when a change is made.
 * Clients should figure out what changed if there are interested at all.
 * This represents a change that is visible from the client.
 * Is deprected because small events (like @{@link de.marcschuler.onyxserver.webclient.messages.channel.ChannelCreateEvent}
 * deliver only the changed parts and a client should figure out how it's affecting the internal server model
 * //TODO remove ServerTreeChangeMessage
 */
@Deprecated
public record ServerTreeChangeMessage(@NotNull ServerDTO server, @NotNull List<SectionExtendedDTO> sections,
                                      @NotNull List<UserSimpleDTO> users,
                                      @NotNull List<UserSimpleDTO> usersNotInChannel) implements MessageBody {

}
