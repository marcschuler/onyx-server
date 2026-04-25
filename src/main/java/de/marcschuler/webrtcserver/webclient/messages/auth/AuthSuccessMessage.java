package de.marcschuler.webrtcserver.webclient.messages.auth;

import de.marcschuler.webrtcserver.dto.data.UserOnlineDTO;
import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthSuccessMessage extends MessageBody {
    @Nullable
    private String message;

    @NotNull
    private String jwt;

    @NotNull
    private UserSimpleDTO me;

    @NotNull
    private List<UserOnlineDTO> clients;
}
