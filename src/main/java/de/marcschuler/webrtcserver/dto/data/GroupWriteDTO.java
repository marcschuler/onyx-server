package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.data.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupWriteDTO {

    @NotNull
    private String name;
    private String description;

    private File icon;

    private UUID parentId;

    @NotNull
    private Map<Permission.PermissionType,Integer> accessPowers;

    @NotNull
    private boolean showInTree;
}
