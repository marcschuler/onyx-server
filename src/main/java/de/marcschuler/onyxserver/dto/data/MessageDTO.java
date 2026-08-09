package de.marcschuler.onyxserver.dto.data;

import de.marcschuler.onyxserver.dto.data.message.MessageContentDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class MessageDTO {
    @NotNull
    private UUID id;
    @NotNull
    private Instant timestamp;
    @NotNull
    private UserSimpleDTO user;

    @NotNull
    private List<MessageContentDTO> content;

    @Nullable
    private MessageDTO repliesTo;

    @NotNull
    private UUID chatId;
}
