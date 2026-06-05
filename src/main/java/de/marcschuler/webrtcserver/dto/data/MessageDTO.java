package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MessageDTO{
    @NotNull
    private UUID id;
    @NotNull
    private Instant timestamp;
    @NotNull
    private UserSimpleDTO user;

    private List<MessageContentDTO> content;
}
