package de.marcschuler.webrtcserver.webclient.messages.error;


import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

//TODO somehow openapi has recursion errors on code generation of we extend from ErrorMessage (make errormessage abstract and take it out of openapi code in config package?)
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class NoPermissionMessage extends MessageBody {
    @NotNull
    private final PermissionType permissionType;
    @Nullable
    private String message;
}
