package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChannelCreateDTO {
    @NotNull
    private String name;
    @NotNull
    private UUID sectionId;
    @NotNull
    private int order;
}
