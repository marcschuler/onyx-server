package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.dto.IceServer;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IceServerMessage extends MessageBody {
    @NotNull
    private List<IceServer> iceServers;

}
