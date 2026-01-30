package de.marcschuler.webrtcserver.data.rules;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public sealed abstract class Policy implements Comparable<Policy> permits SimplePolicy, SpeLPolicy {
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
