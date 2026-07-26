package de.marcschuler.onyxserver.webclient.messages.peer;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * A offer from a client to start a peer connection
 * @param clientTo the client to offer to
 * @param offer the offer //TODO define format
 */
public record PeerOffer(@NotNull String clientTo, @NotNull JsonNode offer) implements MessageBody {
}
