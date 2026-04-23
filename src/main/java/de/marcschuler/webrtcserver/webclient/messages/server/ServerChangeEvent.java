package de.marcschuler.webrtcserver.webclient.messages.server;

import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerChangeEvent {
    private ServerDTO server;
}
