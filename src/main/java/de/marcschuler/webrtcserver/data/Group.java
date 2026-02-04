package de.marcschuler.webrtcserver.data;

import jakarta.persistence.*;
import lombok.Data;

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
}
