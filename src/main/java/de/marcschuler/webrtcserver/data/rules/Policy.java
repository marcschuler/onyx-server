package de.marcschuler.webrtcserver.data.rules;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
public class Policy implements Comparable<Policy> {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private int order;

    private String name;
    private String description;

    private String spel;


    @Override
    public int compareTo(Policy o) {
        return Integer.compare(this.order, o.order);
    }
}
