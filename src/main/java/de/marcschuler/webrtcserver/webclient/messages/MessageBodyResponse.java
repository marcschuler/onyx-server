package de.marcschuler.webrtcserver.webclient.messages;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class MessageBodyResponse<T extends MessageBodyRequest> extends MessageBody {
    @NotNull
    protected UUID respondsTo;
}
