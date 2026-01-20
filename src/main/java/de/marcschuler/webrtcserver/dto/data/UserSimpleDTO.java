package de.marcschuler.webrtcserver.dto.data;

import tools.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSimpleDTO {

    @NotNull
    private String id;
    @NotNull
    private JsonNode publicKey;
    @NotNull
    private String username;
    private FileDTO avatar;
}
