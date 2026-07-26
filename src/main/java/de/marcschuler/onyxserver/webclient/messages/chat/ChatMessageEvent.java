package de.marcschuler.onyxserver.webclient.messages.chat;

import de.marcschuler.onyxserver.dto.data.MessageDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 *  An event that fires when a new message was sent to a chat.
 *  The server may not send you an event for every channel
 * @param chatId the id of the chat the message has been sent
 * @param message the message content
 */
public record ChatMessageEvent(@NotNull UUID chatId, @NotNull MessageDTO message) implements MessageBody {
}
