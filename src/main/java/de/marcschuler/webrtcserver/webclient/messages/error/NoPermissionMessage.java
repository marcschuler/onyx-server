package de.marcschuler.webrtcserver.webclient.messages.error;


import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * A error that fires if the client did something they don't have the permission to
 * @param permissionType the type of permission that has been denied
 * @param message an optional message explaining the error
 */
//TODO somehow openapi has recursion errors on code generation of we extend from ErrorMessage (make errormessage abstract and take it out of openapi code in config package?)
public record NoPermissionMessage(@NotNull PermissionType permissionType,
                                  @Nullable String message) implements MessageBody {
}
