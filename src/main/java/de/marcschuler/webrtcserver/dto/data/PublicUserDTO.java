package de.marcschuler.webrtcserver.dto.data;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublicUserDTO {

    @NotNull
    private String id;
    @NotNull
    private JsonNode publicKey;
    @NotNull
    private String username;
}
