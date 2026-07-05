package de.marcschuler.webrtcserver.dto.data.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MessageCreationDTO {
    @NotNull
    private List<MessageContentDTO> content;

    private UUID repliesTo;
}
