package de.marcschuler.onyxserver.data;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.data.file.File;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "SERVER_USERS")
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

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SectionGroup> sectionGroups = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ChannelGroup> channelGroups = new ArrayList<>();

    @NotNull
    @Column(nullable = false, unique = true, length = 1024)
    private OctetKeyPair publicKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ClientState state;

    @Column(nullable = false)
    private Instant knownSince;
    private Instant lastSeen;

    private boolean owner;
}
