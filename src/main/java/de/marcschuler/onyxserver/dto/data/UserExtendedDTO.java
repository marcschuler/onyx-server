package de.marcschuler.onyxserver.dto.data;

import de.marcschuler.onyxserver.data.ClientState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class UserExtendedDTO extends UserSimpleDTO{

    @NotNull
    private ClientState state;
    @NotNull
    private Instant knownSince;
    private Instant lastSeen;



}
