package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jspecify.annotations.NonNull;

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

    @NonNull
    private List<MessageContentDTO> content;

    @Nullable
    private MessageDTO repliesTo;
}
