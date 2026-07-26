package de.marcschuler.onyxserver.dto.data;

import de.marcschuler.onyxserver.data.file.Hash;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HashDTO {
    @NotNull
    private Hash.HashType type;
    @NotNull
    private String hash;
}
