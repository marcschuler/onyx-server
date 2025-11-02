package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthSuccessMessage extends MessageBody {
    @Nullable
    private String message;

    @NotNull
    private String jwt;
}
