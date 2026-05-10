package de.marcschuler.webrtcserver.webclient.messages.server;

import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An event that fires every time the server changes, e.g. the server name or it's descriptions
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerChangeEvent extends MessageBody {
    @NotNull
    private ServerDTO server;
}
