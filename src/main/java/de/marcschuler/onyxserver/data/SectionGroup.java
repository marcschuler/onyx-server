package de.marcschuler.onyxserver.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class SectionGroup {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Section section;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();
}
