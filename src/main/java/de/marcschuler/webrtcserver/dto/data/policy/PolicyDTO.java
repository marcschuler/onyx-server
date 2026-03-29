package de.marcschuler.webrtcserver.dto.data.policy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class PolicyDTO{
    @NotNull
    protected UUID id;


    @NotNull
    protected String name;
    protected String description;


}
