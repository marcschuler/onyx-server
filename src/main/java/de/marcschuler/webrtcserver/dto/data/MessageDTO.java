package de.marcschuler.webrtcserver.dto.data;

import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class MessageDTO extends MessageWriteDTO{
    private UUID id;
    private Instant timestamp;
    private UserSimpleDTO user;
}
