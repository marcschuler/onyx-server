package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ServerWriteDTO {
    @NotNull
    private String name;
    @NotNull
    private List<MessageContentDTO> description;
}
