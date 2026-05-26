package de.marcschuler.webrtcserver.data.permission;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class Permission {

    @Id
    @GeneratedValue
    private UUID id;

    @ElementCollection(fetch =  FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<PermissionType> permissions;
    private boolean inverted;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Section> limitedToSection;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Channel> limitedToChannel;

    @Column(nullable = false)
    private int priority;
}
