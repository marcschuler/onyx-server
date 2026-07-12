package de.marcschuler.webrtcserver.webclient.messages.peer;

import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A offer from a client to start a peer connection
 * @param clientTo the client to offer to
 * @param offer the offer //TODO define format
 */
public record PeerOffer(@NotNull String clientTo, @NotNull JsonNode offer) implements MessageBody {
}
