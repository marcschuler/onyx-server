package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelCreateDTO extends ChannelWriteDTO{
    @NotNull
    private UUID sectionId;
    @NotNull
    private int order;
}
