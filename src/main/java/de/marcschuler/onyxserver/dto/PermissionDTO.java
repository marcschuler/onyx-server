package de.marcschuler.onyxserver.dto;

import de.marcschuler.onyxserver.data.permission.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private UUID id;

    @NotNull
    private List<PermissionType> permissions;
    @NotNull
    private boolean inverted;
    private List<UUID> limitedToSection;
    private List<UUID> limitedToChannel;

    private int priority;
}
