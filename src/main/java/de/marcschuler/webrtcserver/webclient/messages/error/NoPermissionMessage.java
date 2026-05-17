package de.marcschuler.webrtcserver.webclient.messages.error;


import de.marcschuler.webrtcserver.data.permission.PermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NoPermissionMessage extends ErrorMessage {
    @NotNull
    private final PermissionType permissionType;

    public NoPermissionMessage(PermissionType permissionType, String message) {
        this(permissionType);
        setMessage(message);
    }
}
