package de.marcschuler.webrtcserver.dto;

import de.marcschuler.webrtcserver.webclient.KickReason;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class KickRequestDTO {
    @Nullable
    private KickReason reason;
    @Nullable
    private String message;
}
