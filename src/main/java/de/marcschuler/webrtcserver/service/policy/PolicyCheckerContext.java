package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Permission;
import de.marcschuler.webrtcserver.data.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class PolicyCheckerContext {
    private final Permission.PermissionType permissionType;
    private final User user;
    private final Channel channel;
    private Map<String,Object> data;
}
