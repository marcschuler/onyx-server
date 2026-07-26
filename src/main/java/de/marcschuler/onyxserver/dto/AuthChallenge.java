package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthChallenge {
        @NotNull
        private String challenge;
        @NotNull
        private Instant validUntil;
}
