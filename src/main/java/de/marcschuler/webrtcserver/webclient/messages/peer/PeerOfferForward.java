package de.marcschuler.webrtcserver.webclient.messages.peer;

import tools.jackson.databind.JsonNode;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A peer offer from another client (that send it via @{@link PeerOffer}
 * @param clientFrom the client that sent the offer
 * @param offer the offer //TODO define format
 */
public record PeerOfferForward(@NotNull String clientFrom, @NotNull JsonNode offer) implements MessageBody {
}
