package de.marcschuler.onyxserver.dto;

import de.marcschuler.onyxserver.webclient.KickReason;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class KickRequestDTO {
    @Nullable
    private KickReason reason;
    @Nullable
    private String message;
}
