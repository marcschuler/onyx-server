package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.data.file.File;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.permission.Permission;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.dto.GroupCreateDTO;
import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import de.marcschuler.webrtcserver.mapper.MessageMapper;
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

import java.time.LocalDateTime;
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
    @Autowired
    private MessageMapper messageMapper;

    public Server defaultServer() {
        var servers = serverRepository.findAll();
        if (servers.size() != 1)
            log.error("More than one server active!");
        return servers.get(0);//one should exist at any time
    }

    public List<Server> all() {
        return serverRepository.findAll();
    }


    //TODO replace with SQL script once the start configuration is stable
    @Transactional
    public Server generateDefault() {
        /**
         * BASIC SERVER
         */
        var keys = cryptoService.generateKeyPair();
        var server = new Server();
        server.setName("Onyx Server");
        server.setKeys(keys);
        server.setDescription(List.of(
                new MarkdownMessageContent("""
                        # ONYX
                        You're running your own self-hosted ONYX server instance \uD83E\uDEA8"""),
                new MarkdownMessageContent("""
                        ## Admin Access
                        An invite code was printed to the server log on first startup.
                        
                        To redeem it, open your **user settings** (bottom-left corner), go to **account settings** and enter the code there.
                        
                        > The code is single-use and expires after 14 days."""),
                new MarkdownMessageContent("""
                        ## Documentation
                        
                        - Administrator docs coming soon"""))); //TODO replace with link once available

        /**
         * SECTIONS & CHANNELS
         */
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


        /**
         * GROUPS & PERMISSIONS
         */
        var permissionsAdmin = new Permission();
        permissionsAdmin.setPermissions(Set.of(PermissionType.SERVER, PermissionType.SECTION, PermissionType.CHANNEL, PermissionType.USER, PermissionType.SELF));

        var permissionsMod = new Permission();
        permissionsMod.setPermissions(Set.of(PermissionType.SECTION, PermissionType.CHANNEL, PermissionType.USER_KICK, PermissionType.USER_ACTIVATE, PermissionType.SELF));

        var permissionsUser = new Permission();
        permissionsUser.setPermissions(Set.of(PermissionType.SELF, PermissionType.CHANNEL_JOIN));
        var permissionsUserTeamChannel = new Permission();
        permissionsUserTeamChannel.setPermissions(Set.of(PermissionType.CHANNEL, PermissionType.SELF));
        permissionsUserTeamChannel.setInverted(true);
        permissionsUserTeamChannel.setPriority(100);
        permissionsUserTeamChannel.setLimitedToChannel(Set.of(channel6));

        var groupAdmin = groupService.create(new GroupCreateDTO("Admin", "You can do anything \uD83D\uDE0E", false, true));
        var groupMod = groupService.create(new GroupCreateDTO("Mod", "Manage your server and your user", false, true));
        var groupUser = groupService.create(new GroupCreateDTO("User", "Default group for known users", true, false));

        groupAdmin.setPermissions(List.of(permissionsAdmin));
        groupMod.setPermissions(List.of(permissionsMod));
        groupUser.setPermissions(List.of(permissionsUser, permissionsUserTeamChannel));

        server.setGroups(List.of(groupAdmin, groupMod, groupUser));

        var adminInvite = new Invite();
        adminInvite.setCode(Util.randomCode(16));
        adminInvite.setTitle("Admin Invite");
        adminInvite.setUsages(1);
        adminInvite.setEndDate(LocalDateTime.now().plusDays(14));
        adminInvite.setGroups(List.of(groupAdmin));

        server.setInvites(List.of(adminInvite));
        log.info(" ---------- ADMIN CODE ----------");
        log.info("Your Admin Invite Code is: {}", adminInvite.getCode());
        log.info("Enter it in your app. Do not share it with anyone");
        log.info(" ---------- ADMIN CODE ----------");

        /**
         * SAVE
         */
        return serverRepository.save(server);

    }

    public Optional<Server> get(UUID serverId) {
        return serverRepository.findById(serverId);
    }

    public Server update(Server server, ServerDTO serverDto) {
        server.setName(serverDto.getName());
        if (serverDto.getDescription() != null) {
            server.setDescription(serverDto.getDescription().stream()
                    .map(chatService::createMessageContent)
                    .toList());
        } else {
            server.setDescription(null);
        }
        serverRepository.save(server);
        sendUpdate(server);
        return server;
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
