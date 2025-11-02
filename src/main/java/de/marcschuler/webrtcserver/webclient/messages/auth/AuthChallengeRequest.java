package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/*

 */
@Data
@AllArgsConstructor
public class AuthChallengeRequest extends MessageBody {

    @NotNull
    private final AuthChallenge challenge;
}
