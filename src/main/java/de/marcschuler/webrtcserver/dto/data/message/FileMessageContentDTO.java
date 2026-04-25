package de.marcschuler.webrtcserver.dto.data.message;

import de.marcschuler.webrtcserver.dto.data.FileDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileMessageContentDTO extends MessageContentDTO{
    @NotNull
    private FileDTO file;
}
