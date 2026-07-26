package de.marcschuler.onyxserver.webclient.messages.auth;

import de.marcschuler.onyxserver.dto.data.UserOnlineDTO;
import de.marcschuler.onyxserver.dto.data.UserSimpleDTO;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * The server response for a @{@link AuthChallengeResponse}
 * @param message a admin-defined greeting or notification the client may show
 * @param jwt a jwt to allow access to server resources via REST
 * @param me the description of you
 * @param clients a list of clients which are currently online
 */
public record AuthSuccessMessage(@Nullable String message, @NotNull String jwt, @NotNull UserSimpleDTO me,
                                 @NotNull List<UserOnlineDTO> clients) implements MessageBody {
}
