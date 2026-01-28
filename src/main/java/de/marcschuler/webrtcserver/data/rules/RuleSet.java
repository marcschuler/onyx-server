package de.marcschuler.webrtcserver.data.rules;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class RuleSet {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    private Set<Rule> rules;
}
