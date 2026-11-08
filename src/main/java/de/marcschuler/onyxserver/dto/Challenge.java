package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record Challenge(@NotNull Instant issuedAt, @NotNull String nonce) {
}
