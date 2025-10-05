package de.marcschuler.webrtcserver.service.webclient;

import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.events.server.ServerTreeChangeEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientDataService {

    private final WebClientConnectionService webClientConnectionService;
    private final ServerService serverService;

    private final ServerMapper serverMapper;


    @Transactional
    public ServerTreeChangeEvent createServerTreeChangeEvent(WebClient webClient) {
        return serverMapper.mapToDTO(serverService.getServer());
    }

    @Transactional
    public void updateServerTree() {
        for (WebClient client : webClientConnectionService.clients()) {
            try {
                webClientConnectionService.sendToClient(client, createServerTreeChangeEvent(client));
            } catch (Exception e) {
                log.error("Could not send update", e);
            }
        }
    }
}
