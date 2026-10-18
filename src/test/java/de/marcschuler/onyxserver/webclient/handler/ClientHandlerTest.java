package de.marcschuler.onyxserver.webclient.handler;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.service.ChannelService;
import de.marcschuler.onyxserver.service.PermissionService;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.ClientMessage;
import de.marcschuler.onyxserver.webclient.WebClient;
import de.marcschuler.onyxserver.webclient.WebClientState;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelJoinRequest;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChannelLeaveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientHandlerTest {

    @Mock
    private WebSocketConnectionService webSocketConnectionService;

    @Mock
    private ChannelService channelService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private ClientHandler clientHandler;

    private WebClient client(User user) {
        var client = new WebClient(mock(WebSocketSession.class));
        client.setUser(user);
        client.setState(WebClientState.LOGGED_IN);
        return client;
    }

    @Test
    void onChannelJoinRequest_checksPermissionAndJoinsChannel() {
        var channelId = UUID.randomUUID();
        var channel = new Channel();
        channel.setId(channelId);
        var user = new User();
        user.setId("user-id");
        var client = client(user);
        var request = new ClientChannelJoinRequest(channelId);
        var message = new ClientMessage<>(request, client, LocalDateTime.now());

        when(channelService.get(channelId)).thenReturn(Optional.of(channel));

        clientHandler.onChannelJoinRequest(message);

        verify(permissionService).checkClientAccess(client, channel, PermissionType.CHANNEL_JOIN);
        verify(webSocketConnectionService).joinChannel(client, channel);
    }

    @Test
    void onChannelJoinRequest_missingChannelThrowsException() {
        var channelId = UUID.randomUUID();
        var user = new User();
        var client = client(user);
        var request = new ClientChannelJoinRequest(channelId);
        var message = new ClientMessage<>(request, client, LocalDateTime.now());

        when(channelService.get(channelId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientHandler.onChannelJoinRequest(message));
        verifyNoInteractions(webSocketConnectionService);
    }

    @Test
    void onChannelLeaveRequest_leavesChannel() {
        var user = new User();
        var client = client(user);
        var request = new ClientChannelLeaveRequest();
        var message = new ClientMessage<>(request, client, LocalDateTime.now());

        clientHandler.onChannelLeaveRequest(message);

        verify(webSocketConnectionService).leaveChannel(client);
    }
}
