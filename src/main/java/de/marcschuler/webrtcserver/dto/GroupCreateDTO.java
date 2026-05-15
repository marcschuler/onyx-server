package de.marcschuler.webrtcserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupCreateDTO {
    @NotNull
    private String name;
    @NotNull
    private String description;
}
