package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * An answer for a received @{@link PeerOfferForward}
 * @param clientTo the client to send to
 * @param answer the anser //TODO define format
 */
public record PeerAnswer(@NotNull String clientTo, @NotNull JsonNode answer) implements MessageBody {
}
