package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class PolicyItemDTO {
    private UUID id;

    @NotNull
    private List<PolicyDTO> policies;
}
