package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServerWriteDTO {
    @NotNull
    private String name;
    @NotNull
    private MarkdownMessageContentDTO description;
}
