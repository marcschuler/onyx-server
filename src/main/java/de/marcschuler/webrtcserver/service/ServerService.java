package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.dto.PermissionDTO;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.*;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.messages.server.ServerChangeEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final CryptoService cryptoService;
    @Autowired
    @Lazy
    private WebSocketConnectionService webSocketConnectionService;

    private final GroupService groupService;
    private final ChatService chatService;

    private final ServerRepository serverRepository;
    private final GroupRepository groupRepository;

    private final ServerMapper serverMapper;

    public Server defaultServer() {
        var servers = serverRepository.findAll();
        if (servers.size() != 1)
            log.error("More than one server active!");
        return servers.get(0);//one should exist at any time
    }

    public List<Server> all() {
        return serverRepository.findAll();
    }


    @Transactional
    public Server generateDefault() {
        var keys = cryptoService.generateKeyPair();

        var server = new Server();
        server.setName("Onyx Server");
        server.setKeys(keys); // assuming keys is already defined
        server.setDescription(List.of(new MarkdownMessageContent("This is the default server description.")));


        var permissionsAdmin = new PermissionDTO();
        permissionsAdmin.setPermissions(Set.of(PermissionType.SERVER, PermissionType.SECTION, PermissionType.CHANNEL));

        var permissionsMod = new PermissionDTO();
        permissionsMod.setPermissions(Set.of(PermissionType.SECTION, PermissionType.CHANNEL));
        var group1 = groupService.create(new GroupDTO(null, "Admin", "You can do anything \uD83D\uDE0E", null, null, List.of(permissionsAdmin), true));
        var group2 = groupService.create(new GroupDTO(null, "Mod", "Manage your server and your user", null, null, List.of(permissionsMod), true));
        var group3 = groupService.create(new GroupDTO(null, "User", "Default group for known users", null, null, List.of(), false));

        server.setGroups(List.of(group1, group2, group3));

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

        section1.setChannels(List.of(channel1));
        section2.setChannels(List.of(channel2, channel3));
        section3.setChannels(List.of(channel4, channel5, channel6));

        return serverRepository.save(server);

    }

    public Optional<Server> get(UUID serverId) {
        return serverRepository.findById(serverId);
    }

    public Server update(Server server, ServerDTO serverDto) {
        server = serverMapper.update(server, serverDto);
        sendUpdate(server);
        return server;
    }

    public void save(Server server) {
        this.serverRepository.save(server);
    }

    public void deleteDescription(Server server, UUID descriptionId) {
        server.getDescription().removeIf(s -> s.getId().equals(descriptionId));
        serverRepository.save(server);
    }

    public MessageContent createDescription(Server server, MessageContentDTO messageDto) {
        var content = chatService.createMessageContent(messageDto);
        server.getDescription().add(content);
        serverRepository.save(server);
        sendUpdate(server);
        return content;
    }

    public MessageContent updateDescription(Server server, MessageContent messageContent, MessageContentDTO messageDto) {
        messageContent = chatService.updateMessageContent(messageContent, messageDto);
        serverRepository.save(server);
        sendUpdate(server);
        return messageContent;
    }

    public Optional<MessageContent> descriptionFromId(Server server, UUID id) {
        return server.getDescription().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    public void orderDescription(Server server, MessageContent content, int newOrder) {
        Util.reorder(server.getDescription(), content, newOrder);
        serverRepository.save(server);
        sendUpdate(server);
    }

    private void sendUpdate(Server server) {
        webSocketConnectionService.sendToAll(
                new ServerChangeEvent(serverMapper.mapToDTO(server))
        );
    }


}
