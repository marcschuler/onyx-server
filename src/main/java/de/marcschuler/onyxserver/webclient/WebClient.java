package de.marcschuler.onyxserver.webclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.marcschuler.onyxserver.Util;
import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.error.webclient.PermissionDeniedException;
import de.marcschuler.onyxserver.service.gameintegration.Game;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelJoinEvent;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelLeaveEvent;
import de.marcschuler.onyxserver.webclient.messages.connection.ClientKickEvent;
import de.marcschuler.onyxserver.webclient.messages.connection.KickedEvent;
import de.marcschuler.onyxserver.webclient.messages.gameintegration.GameIntegrationUpdate;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
@Getter
@Slf4j
public class WebClient {
    @JsonIgnore
    private final WebSocketConnectionService webSocketConnectionService;
    @JsonIgnore
    private final WebSocketSession session;
    private final Instant joined = Instant.now();
    private final User user;

    @NotNull
    @Setter
    private WebClientState state;

    @Nullable
    @Setter
    private Channel channel;

    @NotNull
    private final WebClientGameIntegration integration = new WebClientGameIntegration();

    public void setGame(@Nullable Game game) {
        var currentGame = getIntegration().getGame();
        if (currentGame == game)
            return;
        log.info("Updating game for {} to: {}", getUser().getUsername(), game);
        getIntegration().setGame(game);
        webSocketConnectionService.sendToAll(new GameIntegrationUpdate(getUser().getId(), game));
    }

    public void joinChannel(@NonNull Channel channel) throws PermissionDeniedException {
        var currentChannel = getChannel();
        if (currentChannel != null && currentChannel.getId().equals(channel.getId())) {
            log.info("Client wanted to join already used channel {}", channel.getId());
            return;
        }
        this.channel = channel;
        webSocketConnectionService.sendToAll(new ClientChannelJoinEvent(
                getUser().getId(),
                channel.getId()
        ));
    }

    public void leaveChannel() {
        if (getChannel() == null) {
            log.warn("Client {} already is in no channel", this);
            return;
        }
        this.channel = null;
        webSocketConnectionService.sendToAll(new ClientChannelLeaveEvent(getUser().getId()));
    }

    /**
     * Kicks this client
     *
     * @param reason the reason. May be null
     * @param message an admin-defined message related to the kick. May be null
     */
    public void kick(@Nullable KickReason reason, @Nullable String message) {
        this.state = WebClientState.INVALID;
        if (reason == null)
            reason = KickReason.OTHER;
        log.info("Kicking client {} for reason: {}", this, reason);
        send(new KickedEvent(reason, message));
        webSocketConnectionService.remove(this);
        if (getUser() != null) {
            webSocketConnectionService.sendToAll(new ClientKickEvent(getUser().getId(), reason, message));
        }
        try {
            getSession().close(); //TODO check if we can ignore the IOException
        } catch (IOException e) {
            log.error("Could not close session", e);
        }
    }

    public void send(MessageBody messageBody) {
        var data = Util.objectMapper.writeValueAsBytes(messageBody);
        log.debug("Sending to client {}: {}", this, new String(data));
        try {
            synchronized (getSession()) {
                this.session.sendMessage(new TextMessage(data));
            }
        } catch (IOException e) {
            log.error("Could not send message to client: {}", this, e);
            if (getState() != WebClientState.INVALID) {
                kick(KickReason.INTERNAL_ERROR, null);
            }
        }
    }

    @Override
    public String toString() {
        return "WebClient{" +
                ", user=" + user +
                ", state=" + state +
                '}';
    }
}
