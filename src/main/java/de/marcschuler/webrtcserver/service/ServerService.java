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
import jakarta.transaction.Transactional;
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


    @Transactional
    public Server generateDefault() {
        var keys = cryptoService.generateKeyPair();

        // ---------- 1. Create server ----------
        var server = new Server();
        server.setName("WebRTC Server");
        server.setKeys(keys); // assuming keys is already defined
        server.setDescription(new MarkdownMessageContent("This is the default server description."));

        // ---------- 2. Create sections ----------
        var section1 = new Section();
        section1.setName("Lobby");
        section1.setServer(server);

        var section2 = new Section();
        section2.setName("Talk");
        section2.setServer(server);

        var section3 = new Section();
        section3.setName("Chat");
        section3.setServer(server);

        server.setSections(List.of(section1, section2, section3));

        // ---------- 3. Create channels and chats ----------
        var channel1 = new Channel();
        channel1.setName("Lobby");
        var chat1 = new Chat();
        channel1.setChat(chat1);

        var channel2 = new Channel();
        channel2.setName("Talking I");
        var chat2 = new Chat();
        channel2.setChat(chat2);

        var channel3 = new Channel();
        channel3.setName("Talking II");
        var chat3 = new Chat();
        channel3.setChat(chat3);

        var channel4 = new Channel();
        channel4.setName("Chatting");
        var chat4 = new Chat();
        channel4.setChat(chat4);

        var channel5 = new Channel();
        channel5.setName("Other");
        var chat5 = new Chat();
        channel5.setChat(chat5);

        var channel6 = new Channel();
        channel6.setName("Team");
        var chat6 = new Chat();
        channel6.setChat(chat6);

        // ---------- 4. Assign channels to sections ----------
        section1.setChannels(List.of(channel1));
        section2.setChannels(List.of(channel2, channel3));
        section3.setChannels(List.of(channel4, channel5, channel6));

        // ---------- 5. Persist server (cascade saves sections, channels, and chats) ----------
        return serverRepository.save(server);

    }

    public Optional<Server> get(UUID serverId) {
        return serverRepository.findById(serverId);
    }

    public void save(Server server) {
        this.serverRepository.save(server);
        this.webSocketService.updateServerTree();
    }
}
