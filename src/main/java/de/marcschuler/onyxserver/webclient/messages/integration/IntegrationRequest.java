package de.marcschuler.onyxserver.webclient.messages.integration;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;

public record IntegrationRequest(@Nullable String steamId) implements MessageBody {
}
