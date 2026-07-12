package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
/**
 * An event that fires if a section did change
 * @param channel the modified section
 */
public record SectionChangeEvent(@NotNull SectionDTO channel) implements MessageBody {
}
