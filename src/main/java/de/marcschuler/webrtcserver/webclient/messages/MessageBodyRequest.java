package de.marcschuler.webrtcserver.webclient.messages;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class MessageBodyRequest extends MessageBody {
    protected UUID requestId;
}
