package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServerSettingsDTO {

    @NotNull
    private boolean newUsersNeedInvite;

    @NotNull
    private boolean password;
}
