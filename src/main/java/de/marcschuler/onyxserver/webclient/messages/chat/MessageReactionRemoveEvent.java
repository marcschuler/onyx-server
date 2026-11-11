package de.marcschuler.onyxserver.webclient.messages.chat;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * This event fires when a reaction was removed from a message
 * @param chatId the id of the chat the message belongs to
 * @param messageId the id of the message
 * @param reactionId the id of the removed reaction
 */
public record MessageReactionRemoveEvent(@NotNull UUID chatId, @NotNull UUID messageId, @NotNull UUID reactionId) implements MessageBody {
}
