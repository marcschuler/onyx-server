package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionDTO extends SectionWriteDTO {
    @NotNull
    private UUID id;
    @NotNull
    private List<ChannelDTO> channels;

}
