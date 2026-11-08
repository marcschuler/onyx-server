package de.marcschuler.onyxserver.webclient.messages.gameintegration;

import de.marcschuler.onyxserver.service.gameintegration.Game;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record GameIntegrationUpdate(@NotNull String clientId, @Nullable Game game) implements MessageBody {
}
