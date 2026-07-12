package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * A event that fires when a section is moved within a channel
 * @param sectionId the id of the section
 * @param order the new order in the channel
 */
public record SectionMoveEvent(@NotNull UUID sectionId, @NotNull int order) implements MessageBody {
}
