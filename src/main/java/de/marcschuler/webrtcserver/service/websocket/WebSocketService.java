package de.marcschuler.webrtcserver.service.websocket;

import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerTreeChangeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final WebSocketConnectionService webSocketConnectionService;
    private final ServerService serverService;

    private final ServerMapper serverMapper;


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
