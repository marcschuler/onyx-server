package de.marcschuler.webrtcserver.data.rules;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class ItemPolicies {

    @Id
    @GeneratedValue
    private UUID id;

    private Set<Policy> policies;
}
