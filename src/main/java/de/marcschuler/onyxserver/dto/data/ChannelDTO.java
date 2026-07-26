package de.marcschuler.onyxserver.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private UUID sectionId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private int order;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    protected String name;

    @NotNull
    private UUID id;

    @NotNull
    private UUID chatId;

}
