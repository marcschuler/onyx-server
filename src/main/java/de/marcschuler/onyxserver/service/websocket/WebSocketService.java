package de.marcschuler.onyxserver.service.websocket;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.Chat;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.ServerService;
import de.marcschuler.onyxserver.webclient.WebClient;
import de.marcschuler.onyxserver.webclient.messages.server.ServerTreeChangeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final WebSocketConnectionService webSocketConnectionService;
    private final ServerService serverService;

    private final ServerMapper serverMapper;

    public List<WebClient> getClientsInChannel(Channel channel) {
        return getClientsInChannel(channel.getId());
    }

    public List<WebClient> getClientsInChannel(UUID id) {
        return webSocketConnectionService.clientsInteractable()
                .stream().filter(client -> client.getChannel() != null && client.getChannel().getId().equals(id))
                .toList();
    }

    public List<WebClient> getClientsInNoChannel() {
        return webSocketConnectionService.clientsInteractable()
                .stream().filter(client -> client.getChannel() == null)
                .toList();
    }

    public List<WebClient> getClientsInChat(Chat chat) {
        //TODO this expects a chat to be in a channel which may not be true later on
        return webSocketConnectionService.clients()
                .stream().filter(client -> client.getChannel() != null && chat.getId().equals(client.getChannel().getChat().getId()))
                .toList();
    }

    @Deprecated
    @Transactional
    public ServerTreeChangeMessage createServerTreeChangeEvent(WebClient webClient) {
        var users = webSocketConnectionService.users();
        var message = serverMapper.mapToChangeEvent(serverService.defaultServer(),
                serverMapper.mapToDTOList(users),
                serverMapper.mapToDTOList(getClientsInNoChannel().stream().map(WebClient::getUser).toList()));
        message.sections().stream()
                .flatMap(s -> s.getChannels().stream())
                .forEach(c -> {
                    var u = getClientsInChannel(c.getId())
                            .stream().map(WebClient::getUser).toList();
                    c.setUsers(serverMapper.mapToDTOList(u));
                });
        return message;
    }

    @Deprecated
    @Transactional
    public void updateServerTree() {
        for (var client : webSocketConnectionService.clients()) {
            try {
                webSocketConnectionService.send(client, createServerTreeChangeEvent(client));
            } catch (Exception e) {
                log.error("Could not send server tree update", e);
            }
        }
    }
}
