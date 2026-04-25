package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserOnlineDTO{
    @NotNull
    private UserSimpleDTO user;
    private String channelId;
}
