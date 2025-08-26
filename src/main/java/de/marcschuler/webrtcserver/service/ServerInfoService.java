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

    private Server server;

    @PostConstruct
    public void init() {
        var channel1 = new Channel();
        channel1.setName("Channel 1");
        channel1.setId(UUID.randomUUID());
        var channel2 = new Channel();
        channel2.setName("Channel 2");
        channel2.setId(UUID.randomUUID());
        var channel3 = new Channel();
        channel3.setName("Channel 3");
        channel3.setId(UUID.randomUUID());
        var section1 = new Section();
        section1.setName("Start");
        section1.setChannel(List.of(channel1));
        var section2 = new Section();
        section2.setName("Channel");
        section2.setChannel(List.of(channel2, channel3));
        var server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("Server Name");
        server.setSections(List.of(section1, section2));
        server.setDefaultChannel(channel1);
        this.server = server;
    }

    public Server getServerInfov0() {
        return this.server;
    }

    public ServerInfo serverInfo() {
        var details = serverRepository.findAll().stream()
                .map(s -> new ServerInfo.ServerInfoDetail(s.getId(), s.getName(), s.getKeys().getPublic().getEncoded()))
                .toList();
        return new ServerInfo("0.0.0", details);
    }

    public Optional<Channel> channelById(String channelId) {
        return getServerInfov0().getSections().stream()
                .flatMap(section -> section.getChannel().stream())
                .filter(channel -> channel.getId().toString().equals(channelId))
                .findFirst();
    }
}
