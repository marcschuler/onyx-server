package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.dto.IceServer;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * A list of all ice server this server recommends using.
 * Should have at least one entry
 */
public record IceServerMessage(@NotNull List<IceServer> iceServers) implements MessageBody {
}
