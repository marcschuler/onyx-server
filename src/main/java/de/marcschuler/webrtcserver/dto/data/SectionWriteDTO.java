package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionWriteDTO {
    @NotNull
    private String name;
}
