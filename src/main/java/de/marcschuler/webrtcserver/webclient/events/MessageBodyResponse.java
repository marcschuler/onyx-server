package de.marcschuler.webrtcserver.webclient.events;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageBodyResponse<T extends MessageBodyRequest> extends MessageBody {
    @NotNull
    protected UUID respondsTo;
}
