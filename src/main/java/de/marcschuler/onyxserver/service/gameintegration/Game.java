package de.marcschuler.onyxserver.service.gameintegration;

import de.marcschuler.onyxserver.data.file.Hash;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record Game(@NotNull String id,
                   @NotNull String name,
                   @Nullable String color,
                   @Nullable Hash icon) {
}
