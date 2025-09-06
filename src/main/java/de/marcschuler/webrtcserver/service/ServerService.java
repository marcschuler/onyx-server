package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final CryptoService cryptoService;

    private final ServerRepository serverRepository;

    public Server getServer(){
        return serverRepository.findAll().get(0);
    }


    public Server generateDefault() {
        var keys = cryptoService.generateKeyPair();

        var channel1 = new Channel(UUID.randomUUID(), "Lobby");
        var channel2 = new Channel(UUID.randomUUID(), "Talking I");
        var channel3 = new Channel(UUID.randomUUID(), "Talking II");
        var channel4 = new Channel(UUID.randomUUID(), "Chatting");
        var channel5 = new Channel(UUID.randomUUID(), "Other");
        var channel6 = new Channel(UUID.randomUUID(), "Team");

        var section1 = new Section(UUID.randomUUID(), "Lobby", List.of(channel1));
        var section2 = new Section(UUID.randomUUID(), "Talk", List.of(channel2, channel3));
        var section3 = new Section(UUID.randomUUID(), "Chat", List.of(channel4, channel5, channel6));

        var server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("Iris Server");
        server.setKeys(keys);
        server.setSections(List.of(section1, section2, section3));
        server.setDefaultChannel(channel1);

        return serverRepository.save(server);
    }
}
