package de.marcschuler.onyxserver.data.file;

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
