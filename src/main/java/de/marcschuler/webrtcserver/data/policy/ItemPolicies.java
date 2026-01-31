package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class ItemPolicies {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    private Set<Policy> policies;
}
