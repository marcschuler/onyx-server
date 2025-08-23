package de.marcschuler.webrtcserver.dto.auth;

import lombok.Data;

import java.time.Instant;

@Data
public class AuthChallenge {
    private String challenge;
    private Instant expiresIn;
}
