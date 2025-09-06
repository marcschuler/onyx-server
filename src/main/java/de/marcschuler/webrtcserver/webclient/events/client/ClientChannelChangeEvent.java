package de.marcschuler.webrtcserver.webclient.events.client;

import de.marcschuler.webrtcserver.dto.UserReference;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Fires when a user switches a channel.
 * Can be called on a few different occasions
 * (1) use joins the server (channelIdFrom is null)
 * (2) user switches channel (both ids are set)
 * (3) user leaves server (channelIdTo is null)
 */
public class ClientChannelChangeEvent extends EventBody {
    @Nullable
    private UUID channelIdFrom;
    @Nullable
    private UUID channelIdTo;
    @NotNull
    private UserReference user;
}
