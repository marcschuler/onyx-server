package de.marcschuler.webrtcserver.dto.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.JWK;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ServerDTO extends ServerWritableDTO {
    @NotNull
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private JsonNode publicKey;
    private MarkdownMessageContentDTO description;
}
