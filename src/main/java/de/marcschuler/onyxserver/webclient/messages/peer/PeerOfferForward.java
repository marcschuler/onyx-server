package de.marcschuler.onyxserver.webclient.messages.peer;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * A peer offer from another client (that send it via @{@link PeerOffer}
 * @param clientFrom the client that sent the offer
 * @param offer the offer //TODO define format
 */
public record PeerOfferForward(@NotNull String clientFrom, @NotNull JsonNode offer) implements MessageBody {
}
