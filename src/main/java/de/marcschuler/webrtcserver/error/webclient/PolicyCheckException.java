package de.marcschuler.webrtcserver.error.webclient;

import de.marcschuler.webrtcserver.data.Permission;
import lombok.Getter;

public class PolicyCheckException extends RuntimeException {

    @Getter
    private final Permission.PermissionType permissionType;

    public PolicyCheckException(String message, Permission.PermissionType permissionType) {
        super(message);
        this.permissionType = permissionType;
    }

    public PolicyCheckException(String message, Permission.PermissionType permissionType, Throwable cause) {
        super(message, cause);
        this.permissionType = permissionType;
    }
}
