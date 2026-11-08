package de.marcschuler.onyxserver.dto.data;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class UserExtendedDTO extends UserSimpleDTO{

    /**
     * The user has to be activated (e.g. by an invite) before having any permissions
     */
    private boolean pendingAccess;
    /**
     * Set if the user is banned
     */
    @Nullable
    private UserBanDTO ban;
    @NotNull
    private Instant knownSince;
    private Instant lastSeen;



}
