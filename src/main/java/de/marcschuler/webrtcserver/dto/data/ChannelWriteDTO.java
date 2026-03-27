package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.Permission;
import de.marcschuler.webrtcserver.data.policy.PolicyItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ChannelWriteDTO {
    @NotNull
    protected String name;

    @NotNull
    private Map<Permission.PermissionType, PolicyItemDTO> policies;

}
