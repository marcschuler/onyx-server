package de.marcschuler.webrtcserver.dto.data;

import tools.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class UserSimpleDTO {

    @NotNull
    private String id;
    @NotNull
    private Map<String,Object> publicKey;
    @NotNull
    private String username;
    private FileDTO avatar;

    @NotNull
    private Set<GroupDTO> groups;
}
