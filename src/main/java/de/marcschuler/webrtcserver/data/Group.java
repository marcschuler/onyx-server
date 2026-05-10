package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.data.file.File;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Entity(name = "SERVER_GROUP")
@Data
public class Group {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @OneToOne
    private File icon;

    @ManyToOne
    private Group parent;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Map<Permission.PermissionType,Integer> accessPowers;

    private boolean showInTree = true;
}
