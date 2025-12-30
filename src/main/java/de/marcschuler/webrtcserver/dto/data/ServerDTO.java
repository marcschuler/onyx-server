package de.marcschuler.webrtcserver.dto.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import lombok.Data;

import java.util.UUID;

@Data
public class ServerDTO extends ServerWritableDTO {
    private UUID id;
    private String name;
    private JsonNode publicKey;
}
