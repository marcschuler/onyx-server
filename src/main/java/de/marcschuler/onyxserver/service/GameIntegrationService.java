package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.service.gameintegration.SteamGameIntegration;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameIntegrationService {

    private final Optional<SteamGameIntegration> steamGameIntegration;
    private final WebSocketConnectionService webSocketConnectionService;

    @PostConstruct
    void init(){
        if (this.steamGameIntegration.isPresent()){
            log.info("using steam game integration");
        }else {
            log.info("no steam game integration provided");
        }
    }

    @Scheduled(cron = "${onyx.game-integration.steam.scheduler:-}")
    public void updateSteamClients() {
        steamGameIntegration.ifPresent(integration -> {
            var clients = webSocketConnectionService.clients();
            if (clients.isEmpty()) {
                return;
            }
            log.debug("Updating steam game integration for {} clients", clients.size());
            integration.updateClients(clients);
        });
    }
}
