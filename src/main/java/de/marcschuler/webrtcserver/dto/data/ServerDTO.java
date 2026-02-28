package de.marcschuler.webrtcserver.dto.data;

import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ServerDTO extends ServerWriteDTO {
    @NotNull
    private UUID id;
    @NotNull
    private Map<String,Object> publicKey;
}
