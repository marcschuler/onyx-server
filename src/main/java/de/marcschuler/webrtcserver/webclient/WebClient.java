package de.marcschuler.webrtcserver.webclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class WebClient {
    @JsonIgnore
    private final WebSocketSession session;

    @Nullable
    private User user;

    @NotNull
    private WebClientState state;

    @Nullable
    private Channel channel;
}
