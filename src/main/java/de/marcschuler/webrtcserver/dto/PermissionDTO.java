package de.marcschuler.webrtcserver.dto;

import de.marcschuler.webrtcserver.data.permission.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private UUID id;

    @NotNull
    private Set<PermissionType> permissions;
    @NotNull
    private boolean negated;
    private Set<UUID> limitedToSection;
    private Set<UUID> limitedToChannel;
}
