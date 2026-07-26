package de.marcschuler.onyxserver.webclient.messages.auth;

import de.marcschuler.onyxserver.dto.AuthChallenge;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A challenge the client has to sign to validate it's private key
 * @param challenge the challenge data
 */
public record AuthChallengeRequest(@NotNull AuthChallenge challenge) implements MessageBody {

}
