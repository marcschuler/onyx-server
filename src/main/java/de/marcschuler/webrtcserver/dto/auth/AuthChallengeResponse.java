package de.marcschuler.webrtcserver.dto.auth;

import de.marcschuler.webrtcserver.dto.SignedContent;
import lombok.Data;

@Data
public class AuthChallengeResponse {
    private SignedContent<String> challengeResponse;
}
