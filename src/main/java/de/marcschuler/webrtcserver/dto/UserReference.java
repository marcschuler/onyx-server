package de.marcschuler.webrtcserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserReference {

    private String id;
    @NotNull
    private JsonNode publicKey;
    private String username;
}
