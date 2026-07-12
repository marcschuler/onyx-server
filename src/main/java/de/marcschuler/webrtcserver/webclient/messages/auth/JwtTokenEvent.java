package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A scheduled event to replace the jwt token with a new one.
 * @param jwt a fresh jwt token the client should use from now on
 */
public record JwtTokenEvent(@NotNull String jwt) implements MessageBody {
}
