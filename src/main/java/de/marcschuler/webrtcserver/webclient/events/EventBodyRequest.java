package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

import java.util.UUID;

@Data
public class EventBodyRequest extends EventBody{
    protected UUID requestId;
}
