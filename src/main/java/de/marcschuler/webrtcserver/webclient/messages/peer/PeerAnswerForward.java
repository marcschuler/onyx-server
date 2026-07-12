package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * The answer from a peer
 * @param clientFrom the client that did send it
 * @param answer the anser //TODO define format
 */
public record PeerAnswerForward(@NotNull String clientFrom, @NotNull JsonNode answer) implements MessageBody {
}
