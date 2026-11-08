package de.marcschuler.onyxserver.dto;

import jakarta.validation.constraints.NotNull;

public record AuthChallenge(
        @NotNull Challenge challenge) {
}
