package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @NotNull
    private HashType type;
    @NotNull
    private String hash;

    @RequiredArgsConstructor
    @Getter
    public enum HashType{
        SHA_256("sha256");

        private final String safeName;
    }
}
