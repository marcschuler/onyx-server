package de.marcschuler.webrtcserver.webclient.events.client;

import de.marcschuler.webrtcserver.dto.UserReference;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Signals that a client that previously may or may not was in another channel
 * joins a (new) channel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientChannelJoinEvent extends EventBody {
    @NotNull
    private UserReference user;
    @Nullable
    private UUID channelId;
}
