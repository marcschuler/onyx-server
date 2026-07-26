package de.marcschuler.onyxserver.service.gameintegration;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ClientCurrentGame(Integration integration, @NotNull String gameName, @Nullable String gameState) {
    public enum Integration {
        STEAM
    }
}
