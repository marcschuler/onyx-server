package de.marcschuler.onyxserver.webclient.messages.section;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 *  An event that fires when a section is deleted.
 *  As no section data will be left only the section id is given.
 *  The server guarantees that no channel is within that section when it is deleted
 * @param sectionId the id of the section
 */
public record SectionDeleteEvent(@NotNull UUID sectionId) implements MessageBody {
}
