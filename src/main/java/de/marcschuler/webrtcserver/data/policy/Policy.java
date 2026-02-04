package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(
        name = "type",
        discriminatorType = DiscriminatorType.STRING
)
@Data
public abstract class Policy implements Comparable<Policy> {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private int order;

    private String name;
    private String description;


    @Override
    public int compareTo(Policy o) {
        return Integer.compare(this.order, o.order);
    }
}
