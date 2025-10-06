package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenMessage extends EventBody {
    @NotNull
    private String token;
}
