package de.marcschuler.webrtcserver.dto.data.policy;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class PolicyWriteDTO {

    @NotNull
    protected int order;

    @NotNull
    protected String name;
    protected String description;
}
