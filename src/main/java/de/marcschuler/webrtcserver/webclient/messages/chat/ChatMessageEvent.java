package de.marcschuler.webrtcserver.webclient.messages.chat;

import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * An event that fires when a new message was sent to a chat you currently watch
 * The server may not send you an event for every channel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent extends MessageBody {
    @NotNull
    private UUID chatId;
    @NotNull
    private MessageDTO message;
}
