package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ServerDTO extends ServerWriteDTO {
    @NotNull
    private UUID id;
    @NotNull
    private Map<String,Object> publicKey;
    @NotNull
    private List<MessageContentDTO> description;
}
