package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 *  The client response for a @{@link AuthChallengeRequest} with the preferred username, his public key
 *     and the signed challenge to authenticate against
 * @param publicKey the client's public key as used to sign the challenge
 * @param challenge the signed challenge the server did send
 * @param username the preferred username. the server may pick another
 */
public record AuthChallengeResponse(@NotNull Map<String, Object> publicKey, @NotNull SignedContent challenge,
                                    @NotNull String username) implements MessageBody {
}
