package de.marcschuler.webrtcserver.webclient.messages.peer;

import de.marcschuler.webrtcserver.dto.IceServer;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.Data;

import java.util.List;

@Data
public class IceServerMessage extends MessageBody {
    private List<IceServer> iceServers;

}
