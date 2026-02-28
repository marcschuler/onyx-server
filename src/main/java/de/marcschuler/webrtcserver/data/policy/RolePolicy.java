package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@DiscriminatorValue("SIMPLE")
@Entity
@Data
public class RolePolicy extends Policy {
    @Column(nullable = false)
    private SimplePolicyOperand operand;
    @Column(nullable = false)
    private SimplePolicyOperator operator;
    @Column(nullable = false)
    private Set<UUID> ids;


    public enum SimplePolicyOperand {
        GROUP,
        USER
    }

    public enum SimplePolicyOperator {
        IN,
        NOT_IN,
        IN_ALL
    }
}
