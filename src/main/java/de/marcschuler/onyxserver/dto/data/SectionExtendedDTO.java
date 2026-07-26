package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionExtendedDTO {
    @NotNull
    private String name;
    @NotNull
    private UUID id;
    @NotNull
    private List<ChannelExtendedDTO> channels;
}
