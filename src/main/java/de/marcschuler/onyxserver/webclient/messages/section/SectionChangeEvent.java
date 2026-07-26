package de.marcschuler.onyxserver.webclient.messages.section;

import de.marcschuler.onyxserver.dto.data.SectionDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
/**
 * An event that fires if a section did change
 * @param channel the modified section
 */
public record SectionChangeEvent(@NotNull SectionDTO channel) implements MessageBody {
}
