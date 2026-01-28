package de.marcschuler.webrtcserver.data.rules;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
public class Rule {
    @Id
    @GeneratedValue
    private UUID id;

    private RuleType type;
    private RuleComparison comparison;
    private RuleThen thenDo; //allow-deny
}
