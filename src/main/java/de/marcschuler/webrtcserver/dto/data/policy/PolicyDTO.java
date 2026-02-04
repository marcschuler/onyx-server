package de.marcschuler.webrtcserver.dto.data.policy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimplePolicyDTO.class, name = "SIMPLE"),
})
@Data
public class PolicyDTO extends PolicyWriteDTO {
    @NotNull
    private UUID id;


}
