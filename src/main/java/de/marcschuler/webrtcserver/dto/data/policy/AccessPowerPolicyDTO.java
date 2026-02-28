package de.marcschuler.webrtcserver.dto.data.policy;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccessPowerPolicyDTO {
    @NotNull
    private int accessPower;
}
