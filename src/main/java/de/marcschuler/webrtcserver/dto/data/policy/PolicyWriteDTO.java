package de.marcschuler.webrtcserver.dto.data.policy;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PolicyWriteDTO {

    @NotNull
    private int order;

    @NotNull
    private String name;
    private String description;
}
