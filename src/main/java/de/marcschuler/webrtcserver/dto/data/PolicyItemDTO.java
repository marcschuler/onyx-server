package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class PolicyItemDTO {
    private UUID id;

    @NotNull
    private List<PolicyDTO> policies;
}
