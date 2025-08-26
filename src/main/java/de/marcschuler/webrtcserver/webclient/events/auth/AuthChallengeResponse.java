package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthChallengeResponse extends EventBody {
    @NotNull
    private String publicKey;
    @NotNull
    private SignedContent<AuthService.AuthChallenge> challenge;
    @NotNull
    private String username;
    @NotNull
    private String signature;
}
