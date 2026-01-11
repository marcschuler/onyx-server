package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.ChatRepository;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.repository.ServerRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
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

    private final CryptoService cryptoService;
    @Autowired
    @Lazy
    private WebSocketService webSocketService;

    private final ServerRepository serverRepository;
    private final ChannelRepository channelRepository;
    private final SectionRepository sectionRepository;
    private final ChatRepository chatRepository;

    private final ServerMapper serverMapper;

    public Server defaultServer() {
        return serverRepository.findAll().get(0); //one should exist at any time
    }

    public List<Server> all() {
        return serverRepository.findAll();
    }


    public Server generateDefault() {
        var keys = cryptoService.generateKeyPair();

        var server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("WebRTC Server");
        server.setKeys(keys);
        server.setDescription(new MarkdownMessageContent("This is the default server description."));


        var section1 = new Section(UUID.randomUUID(), "Lobby", List.of(), server);
        var section2 = new Section(UUID.randomUUID(), "Talk", List.of(), server);
        var section3 = new Section(UUID.randomUUID(), "Chat", List.of(), server);


        var chat1 = new Chat(UUID.randomUUID(), List.of());
        var chat2 = new Chat(UUID.randomUUID(), List.of());
        var chat3 = new Chat(UUID.randomUUID(), List.of());
        var chat4 = new Chat(UUID.randomUUID(), List.of());
        var chat5 = new Chat(UUID.randomUUID(), List.of());
        var chat6 = new Chat(UUID.randomUUID(), List.of());


        var channel1 = new Channel(UUID.randomUUID(), "Lobby", chat1, section1);
        var channel2 = new Channel(UUID.randomUUID(), "Talking I", chat2, section2);
        var channel3 = new Channel(UUID.randomUUID(), "Talking II", chat3, section2);
        var channel4 = new Channel(UUID.randomUUID(), "Chatting", chat4, section3);
        var channel5 = new Channel(UUID.randomUUID(), "Other", chat5, section3);
        var channel6 = new Channel(UUID.randomUUID(), "Team", chat6, section3);

        serverRepository.save(server);
        sectionRepository.saveAll(List.of(section1, section2, section3));
        chatRepository.saveAll(List.of(chat1, chat2, chat3, chat4, chat5, chat6));
        channelRepository.save(channel1);
        channelRepository.saveAll(List.of(channel1, channel2, channel3, channel4, channel5, channel6));


        return serverRepository.findById(server.getId()).orElseThrow();
    }

    public Optional<Server> get(UUID serverId) {
        return serverRepository.findById(serverId);
    }

    public void save(Server server) {
        this.serverRepository.save(server);
        this.webSocketService.updateServerTree();
    }
}
