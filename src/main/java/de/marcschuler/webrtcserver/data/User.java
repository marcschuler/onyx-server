package de.marcschuler.webrtcserver.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.security.PublicKey;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "server_users")
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

    @ManyToMany(fetch =  FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

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
