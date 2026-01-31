package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @NotNull
    private HashType type;
    @NotNull
    private String hash;

    public enum HashType{
        SHA_256
    }
}
