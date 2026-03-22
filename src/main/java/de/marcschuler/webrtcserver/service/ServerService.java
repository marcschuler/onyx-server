package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import de.marcschuler.webrtcserver.dto.data.GroupWriteDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.dto.data.policy.RolePolicyDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.*;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
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
    private WebSocketService webSocketService;

    private final GroupService groupService;
    private final PolicyService policyService;

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
        server.setDescription(new MarkdownMessageContent("This is the default server description."));

        //TODO create default AccessPowerPolicies for the channels


        var group1 = groupService.create(new GroupWriteDTO("Admin", "A Administrator is allowed to to anything", null, null, Map.of()));
        var group2 = groupService.create(new GroupWriteDTO("Mod", "A Moderator is allowed to moderate users", null, null, Map.of()));
        var group3 = groupService.create(new GroupWriteDTO("User", "Default group for known users", null, null, Map.of()));

        server.setGroups(List.of(group1, group2, group3));

        var policy1 = policyService.create(RolePolicyDTO.builder()
                .operator(RolePolicy.SimplePolicyOperator.IN)
                .operand(RolePolicy.SimplePolicyOperand.GROUP)
                .ids(Set.of(group1.getId()))
                .priority(100)
                .name("Only Admins")
                .build());
        var policy2 = policyService.create(RolePolicyDTO.builder()
                .operator(RolePolicy.SimplePolicyOperator.IN)
                .operand(RolePolicy.SimplePolicyOperand.GROUP)
                .ids(Set.of(group1.getId(), group2.getId()))
                .priority(50)
                .name("Admins + Mods")
                .build());
        var policy3 = policyService.create(RolePolicyDTO.builder()
                .operator(RolePolicy.SimplePolicyOperator.IN)
                .operand(RolePolicy.SimplePolicyOperand.GROUP)
                .ids(Set.of(group1.getId(), group2.getId(), group3.getId()))
                .name("Admins + Mods + User")
                .priority(10)
                .build());

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

    public void save(Server server) {
        this.serverRepository.save(server);
        this.webSocketService.updateServerTree();
    }
}
