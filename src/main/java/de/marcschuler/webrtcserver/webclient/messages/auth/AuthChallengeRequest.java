package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

/**
 * A challenge the client has to sign to validate it's private key
 * @param challenge the challenge data
 */
public record AuthChallengeRequest(@NotNull AuthChallenge challenge) implements MessageBody {

}
