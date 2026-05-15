package de.marcschuler.webrtcserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NotNull
@Data
public class SectionCreateDTO {
    @NotNull
    private String name;
}
