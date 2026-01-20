package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.security.PublicKey;
import java.time.Instant;

@Entity(name = "user_table")//TODO terrible fix
@Data
public class User {
    @Id
    @NotNull
    private String id;
    @NotNull
    @Size(min = 3, max = 32)
    private String username;
    @OneToOne
    private File avatar;

    @NotNull
    @Column(nullable = false, unique = true, length = 1024)
    private PublicKey publicKey;

    @NotNull
    private ClientState state;

    @Column(nullable = false)
    private Instant knownSince;
    private Instant lastSeen;

    private boolean owner;
}
