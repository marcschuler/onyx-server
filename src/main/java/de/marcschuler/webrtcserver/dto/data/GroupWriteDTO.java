package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.data.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class GroupWriteDTO {

    @NotNull
    private String name;
    private String description;

    private File icon;

    private UUID parentId;

    private Map<Permission.PermissionType,Integer> permissions;
}
