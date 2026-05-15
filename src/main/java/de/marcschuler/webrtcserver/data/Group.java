package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.data.file.File;
import de.marcschuler.webrtcserver.data.permission.Permission;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name = "SERVER_GROUP")
@Data
public class Group implements Comparable<Group> {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @OneToOne
    private File icon;

    private int priority;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Group> inheritsFrom;


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Permission> permissions;

    private boolean label = true;

    @Override
    public int compareTo(Group o) {
        return Integer.compare(this.priority, o.priority);
    }
}
