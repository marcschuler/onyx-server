package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SimpleGroupDTO {
    @NotNull
    private UUID id;

    @NotNull
    private String name;
}
