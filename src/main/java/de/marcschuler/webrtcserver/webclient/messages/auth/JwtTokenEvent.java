package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenEvent extends MessageBody {
    @NotNull
    private String jwt;
}
