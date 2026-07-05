package de.marcschuler.webrtcserver.webclient.messages.error;


import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO somehow openapi has recursion errors on code generation of we extend from ErrorMessage (make errormessage abstract and take it out of openapi code in config package?)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoPermissionMessage extends MessageBody {
    @NotNull
    private PermissionType permissionType;
    @Nullable
    private String message;

    public NoPermissionMessage(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
