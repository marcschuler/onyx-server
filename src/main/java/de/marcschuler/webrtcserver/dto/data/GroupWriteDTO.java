package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.File;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupWriteDTO {

    @NotNull
    private String name;
    private String description;

    private File icon;

    private UUID parentId;
}
