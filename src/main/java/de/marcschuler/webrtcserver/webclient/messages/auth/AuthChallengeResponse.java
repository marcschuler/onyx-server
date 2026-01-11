package de.marcschuler.webrtcserver.webclient.messages.auth;

import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
    The client response with the preferred username, his public key
    and the signed challenge to authenticate against
 */
@Data
public class AuthChallengeResponse extends MessageBody {
    @NotNull
    private JsonNode publicKey;
    @NotNull
    private SignedContent challenge;
    @NotNull
    private String username;

}
