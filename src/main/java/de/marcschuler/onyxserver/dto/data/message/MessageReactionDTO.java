package de.marcschuler.onyxserver.dto.data.message;

import de.marcschuler.onyxserver.dto.data.UserSimpleDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MessageReactionDTO {
    @NotNull
    private UUID id;
    @NotNull
    private UserSimpleDTO user;
    @NotNull
    private Instant timestamp;
    @NotNull
    private String emoji;
    @NotNull
    private UUID messageId;
}
