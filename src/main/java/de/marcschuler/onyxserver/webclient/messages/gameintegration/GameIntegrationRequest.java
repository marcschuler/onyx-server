package de.marcschuler.onyxserver.webclient.messages.gameintegration;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;

public record GameIntegrationRequest(@Nullable Long steamId64) implements MessageBody {
}
