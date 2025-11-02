package de.marcschuler.webrtcserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserReference {

    @NotNull
    private String id;
    @NotNull
    private JsonNode publicKey;
    @NotNull
    private String username;
}
