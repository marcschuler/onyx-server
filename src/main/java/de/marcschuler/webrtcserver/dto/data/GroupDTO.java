package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupDTO extends GroupWriteDTO {
    @NotNull
    private UUID id;
}
