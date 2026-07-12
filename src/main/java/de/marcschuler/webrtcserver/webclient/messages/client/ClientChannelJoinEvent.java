package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 *  Signals that a client that previously may or may not was in another channel
 *  joins a (new) channel
 * @param userId the id of the user that did join a channel
 * @param channelId the id of the channel
 */
public record ClientChannelJoinEvent(@NotNull String userId, @NotNull UUID channelId) implements MessageBody {
}
