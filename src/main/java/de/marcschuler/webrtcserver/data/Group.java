package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Group {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @ManyToOne
    private Group parent;
}
