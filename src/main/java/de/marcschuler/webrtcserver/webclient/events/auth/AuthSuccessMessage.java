package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class AuthSuccessMessage extends MessageBody {
    @Nullable
    private String message;

    private String jwt;
}
