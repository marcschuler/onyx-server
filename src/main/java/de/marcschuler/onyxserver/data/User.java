package de.marcschuler.onyxserver.data;

import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.data.file.File;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

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

    /**
     * The user has to be activated (e.g. by an invite) before having any permissions
     */
    @Column(nullable = false)
    private boolean pendingAccess;

    /**
     * Set if the user is banned, null otherwise
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private UserBan ban;

    @Column(nullable = false)
    private Instant knownSince;
    private Instant lastSeen;

    private boolean owner;

    /**
     * @return true if the user is banned
     */
    public boolean isBanned() {
        return ban != null;
    }
}
