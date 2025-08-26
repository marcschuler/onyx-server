package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.security.PublicKey;
import java.time.Instant;

@Email
@Data
public class User {
    @Id
    @NotNull
    private String id;
    @NotNull
    @Size(min = 3, max = 32)
    private String username;

    @NotNull
    @Column(nullable = false, unique = true)
    private PublicKey publicKey;

    @NotNull
    private ClientState state;

    @Column(nullable = false)
    private Instant knownSince;
    private Instant lastSeen;
}
