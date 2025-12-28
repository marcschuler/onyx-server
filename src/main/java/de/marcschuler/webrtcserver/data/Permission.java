package de.marcschuler.webrtcserver.data;

import lombok.Data;

@Data
public class Permission {
    private String type;

    public enum PermissionType{
        SERVER,
        SECTION,
        CHANNEL
    }
}
