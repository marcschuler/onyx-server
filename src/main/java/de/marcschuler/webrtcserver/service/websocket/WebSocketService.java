package de.marcschuler.webrtcserver.service.websocket;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerTreeChangeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final WebSocketConnectionService webSocketConnectionService;
    private final ServerService serverService;

    private final ServerMapper serverMapper;

    public List<WebClient> getClientsInChannel(Channel channel) {
        return webSocketConnectionService.clients()
                .stream().filter(client -> channel.equals(client.getChannel()))
                .toList();
    }

    public List<WebClient> getClientsInChat(Chat chat) {
        //TODO this expects a chat to be in a channel which may not be true later on
        return webSocketConnectionService.clients()
                .stream().filter(client -> client.getChannel() != null && chat.getId().equals(client.getChannel().getChat().getId()))
                .toList();
    }

    @Transactional
    public ServerTreeChangeMessage createServerTreeChangeEvent(WebClient webClient) {
        return serverMapper.mapToChangeEvent(serverService.defaultServer());
    }

    @Transactional
    public void updateServerTree() {
        for (WebClient client : webSocketConnectionService.clients()) {
            try {
                webSocketConnectionService.sendToClient(client, createServerTreeChangeEvent(client));
            } catch (Exception e) {
                log.error("Could not send server tree update", e);
            }
        }
    }
}
