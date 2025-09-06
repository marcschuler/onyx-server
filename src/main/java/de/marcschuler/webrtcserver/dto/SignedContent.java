package de.marcschuler.webrtcserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedContent<T> {
    @NotNull
    private String content;
    @NotNull
    private String contentSignature;
}
