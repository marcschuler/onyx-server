package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.file.Hash;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HashDTO {
    @NotNull
    private Hash.HashType type;
    @NotNull
    private String hash;
}
