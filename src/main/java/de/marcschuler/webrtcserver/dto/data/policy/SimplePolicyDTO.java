package de.marcschuler.webrtcserver.dto.data.policy;

import de.marcschuler.webrtcserver.data.policy.SimplePolicy;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public final class SimplePolicyDTO extends PolicyDTO{
    @NotNull
    private SimplePolicy.SimplePolicyOperand operand;
    @NotNull
    private SimplePolicy.SimplePolicyOperator operator;
    @NotNull
    private Set<UUID> ids;
}
