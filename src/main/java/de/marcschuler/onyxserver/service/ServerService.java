package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.Server;
import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.repository.ServerRepository;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.server.ServerChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    @Autowired
    @Lazy
    private WebSocketConnectionService webSocketConnectionService;

    private final ChatService chatService;

    private final ServerRepository serverRepository;

    private final ServerMapper serverMapper;

    public Server defaultServer() {
        var servers = serverRepository.findAll();
        if (servers.size() != 1)
            log.error("More than one server active!");
        return servers.getFirst();//one should exist at any time
    }

    public List<Server> all() {
        return serverRepository.findAll();
    }


    public Optional<Server> get(UUID serverId) {
        return serverRepository.findById(serverId);
    }

    public Server update(Server server, ServerDTO serverDto) {
        server.setName(serverDto.getName());
        if (serverDto.getDescription() != null) {
            server.getDescription().clear();
            serverDto.getDescription().stream()
                    .map(chatService::createMessageContent)
                    .forEach(s -> server.getDescription().add(s));
        } else {
            server.getDescription().clear();
        }
        serverRepository.save(server);
        sendUpdate(server);
        return server;
    }

    public void setServerPassword(Server server, String password) {
        log.info("Changed server password: {} password", password == null ? "removed" : "added");
        server.getSettings().setPassword(password);
        serverRepository.save(server);
        sendUpdate(server);
    }

    public void save(Server server) {
        this.serverRepository.save(server);
    }

    public void setIcon(Server server, File f) {
        server.setIcon(f);
        serverRepository.save(server);
        sendUpdate(server);
    }

    private void sendUpdate(Server server) {
        webSocketConnectionService.sendToAll(
                new ServerChangeEvent(serverMapper.mapToDTO(server))
        );
    }


}
