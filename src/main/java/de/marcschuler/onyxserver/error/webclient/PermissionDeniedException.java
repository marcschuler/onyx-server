package de.marcschuler.onyxserver.error.webclient;

import de.marcschuler.onyxserver.data.permission.PermissionType;
import lombok.Getter;

public class PermissionDeniedException extends RuntimeException {

    @Getter
    private final PermissionType permissionType;

    public PermissionDeniedException(String message, PermissionType permissionType) {
        super(message);
        this.permissionType = permissionType;
    }

    public PermissionDeniedException(String message, PermissionType permissionType, Throwable cause) {
        super(message, cause);
        this.permissionType = permissionType;
    }
}
