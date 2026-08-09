package de.marcschuler.onyxserver.webclient.messages.chat;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * This event fires when a message has been deleted.
 * If a client currently shows this message it is expected to be deleted instantly
 * @param id the id of the deleted message
 */
public record MessageDeleteEvent(@NotNull UUID id) implements MessageBody {
}
