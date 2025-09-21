package de.marcschuler.webrtcserver.webclient.events;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EventBodyResponse <T extends EventBodyRequest> extends EventBody {
    @NotNull
    protected UUID respondsTo;
}
