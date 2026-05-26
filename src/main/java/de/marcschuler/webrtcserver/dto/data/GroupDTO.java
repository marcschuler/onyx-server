package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.file.File;
import de.marcschuler.webrtcserver.data.permission.Permission;
import de.marcschuler.webrtcserver.dto.PermissionDTO;
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
public class GroupDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private UUID id;

    @NotNull
    private String name;
    private String description;

    private FileDTO icon;

    private List<GroupDTO> inheritsFrom;

    @NotNull
    private int priority;
    @NotNull
    private boolean defaultForNewUsers;


    private List<PermissionDTO> permissions;

    @NotNull
    private boolean label;
}
