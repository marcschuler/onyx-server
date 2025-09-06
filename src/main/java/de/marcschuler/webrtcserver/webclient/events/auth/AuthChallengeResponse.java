package de.marcschuler.webrtcserver.webclient.events.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.service.AuthService;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
    The client response with the preferred username, his public key
    and the signed challenge to authenticate against
 */
@Data
public class AuthChallengeResponse extends EventBody {
    @NotNull
    private JsonNode publicKey;
    @NotNull
    private SignedContent<AuthService.AuthChallenge> challenge;
    @NotNull
    private String username;

}
