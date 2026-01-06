package de.marcschuler.webrtcserver.webclient.messages.chat;

import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeMessageEvent extends MessageBody {
    @NotNull
    private UUID chatId;
    @NotNull
    private MessageDTO message;
}
