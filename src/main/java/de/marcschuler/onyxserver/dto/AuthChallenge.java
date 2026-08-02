package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AuthChallenge(
        @NotNull String challenge,
        @NotNull Instant validUntil) {
}
