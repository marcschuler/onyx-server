package de.marcschuler.webrtcserver.dto.data.policy;

import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public final class RolePolicyDTO extends PolicyDTO{
    @NotNull
    private RolePolicy.SimplePolicyOperand operand;
    @NotNull
    private RolePolicy.SimplePolicyOperator operator;
    @NotNull
    private Set<UUID> ids;
}
