package de.marcschuler.onyxserver.dto.data;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

/**
 * A ban of a user
 */
@Data
public class UserBanDTO {
    /**
     * Reason shown to the banned user
     */
    @Nullable
    private String reason;
    @NotNull
    private Instant bannedAt;
    /**
     * Id of the user who issued the ban, null if banned by the system
     */
    @Nullable
    private String bannedBy;
}
