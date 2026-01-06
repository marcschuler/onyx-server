package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDTO extends ChannelWriteDTO {
    @NotNull
    private UUID id;

    @NotNull
    private UUID chatId;
}
