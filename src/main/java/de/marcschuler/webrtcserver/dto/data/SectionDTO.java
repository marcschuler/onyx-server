package de.marcschuler.webrtcserver.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private String name;

    @NotNull
    private UUID id;
    @NotNull
    private List<ChannelDTO> channels;

}
