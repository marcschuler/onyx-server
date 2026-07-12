package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * A request from a client to change the channel.
 * The server may not allow this.
 * If the server allows this, a @{@link ClientChannelJoinEvent} is sent
 */
public record ClientChannelJoinRequest(@NotNull UUID channelId) implements MessageBody {

}
