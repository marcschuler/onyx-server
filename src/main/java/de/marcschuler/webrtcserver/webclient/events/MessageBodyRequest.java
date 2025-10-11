package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageBodyRequest extends MessageBody {
    protected UUID requestId;
}
