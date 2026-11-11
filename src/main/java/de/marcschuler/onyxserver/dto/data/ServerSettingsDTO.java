package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ServerSettingsDTO {

    @NotNull
    private boolean newUsersNeedInvite;

    @NotNull
    private boolean password;

    /**
     * The emojis users may react to messages with. An empty list allows any emoji
     */
    private List<String> reactionEmojis;
}
