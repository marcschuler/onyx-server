package de.marcschuler.webrtcserver.dto.data.policy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RolePolicyDTO.class, name = "ROLE")
})
@Data
@NoArgsConstructor
@SuperBuilder
public class PolicyDTO extends PolicyWriteDTO {
    @NotNull
    protected UUID id;


}
