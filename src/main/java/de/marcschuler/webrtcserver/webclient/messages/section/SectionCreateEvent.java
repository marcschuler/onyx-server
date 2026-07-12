package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.dto.data.SectionExtendedDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A event that fires when a new section is created
 * @param section the section
 * @param order the order of the section within the server
 */
public record SectionCreateEvent(@NotNull SectionExtendedDTO section, @NotNull int order) implements MessageBody {
}
