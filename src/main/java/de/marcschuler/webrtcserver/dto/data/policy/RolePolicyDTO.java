package de.marcschuler.webrtcserver.dto.data.policy;

import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Data
public final class RolePolicyDTO extends PolicyDTO{
    @NotNull
    private RolePolicy.SimplePolicyOperand operand;
    @NotNull
    private RolePolicy.SimplePolicyOperator operator;
    @NotNull
    private Set<UUID> ids;
}
