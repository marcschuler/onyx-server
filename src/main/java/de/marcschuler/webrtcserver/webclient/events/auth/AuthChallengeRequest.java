package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthChallengeRequest extends EventBody {

    @NotNull
    private final AuthService.AuthChallenge challenge;
}
