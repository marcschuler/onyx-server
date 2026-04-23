package de.marcschuler.webrtcserver.webclient.messages.client;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientServerLeaveEvent extends MessageBody {
    @NotNull
    private String userId;
}
