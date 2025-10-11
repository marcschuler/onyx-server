package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.webclient.events.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenMessage extends MessageBody {
    @NotNull
    private String token;
}
