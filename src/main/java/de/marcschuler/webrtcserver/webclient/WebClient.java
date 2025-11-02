package de.marcschuler.webrtcserver.webclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

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

    public void sendMessage(MessageBody messageBody) throws IOException {
        var data = Util.objectMapper.writeValueAsBytes(messageBody);
        this.session.sendMessage(new TextMessage(data));
    }
}
