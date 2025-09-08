package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.dto.ServerInfo;
import de.marcschuler.webrtcserver.repository.ServerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerInfoService {

    private final ServerRepository serverRepository;


    @Deprecated
    public Server getServerInfov0() {
        return this.serverRepository.findAll().get(0);
    }

    public ServerInfo serverInfo() {
        var details = serverRepository.findAll().stream()
                .map(s -> new ServerInfo.ServerInfoDetail(s.getId(), s.getName(), s.getKeys().getPublic().getEncoded(), -1))
                .toList();
        return new ServerInfo("0.0.0", details);
    }

    public Optional<Channel> channelById(UUID channelId) {
        return getServerInfov0().getSections().stream()
                .flatMap(section -> section.getChannels().stream())
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst();
    }
}
