package de.marcschuler.onyxserver.webclient.messages.chat;

import de.marcschuler.onyxserver.dto.data.message.MessageReactionDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * This event fires when a user added a reaction to a message
 * @param chatId the id of the chat the message belongs to
 * @param reaction the new reaction
 */
public record MessageReactionAddEvent(@NotNull UUID chatId, @NotNull MessageReactionDTO reaction) implements MessageBody {
}
