package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/*
    The client response with the preferred username, his public key
    and the signed challenge to authenticate against
 */
@Data
public class AuthChallengeResponse extends MessageBody {
    @NotNull
    private Map<String,Object> publicKey;
    @NotNull
    private SignedContent challenge;
    @NotNull
    private String username;

}
