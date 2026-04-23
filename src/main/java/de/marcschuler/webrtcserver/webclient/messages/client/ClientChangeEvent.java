package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientChangeEvent extends MessageBody {
    private UserSimpleDTO user;
}
