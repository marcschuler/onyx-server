package de.marcschuler.onyxserver.config.liquibase;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.Group;
import de.marcschuler.onyxserver.data.Section;
import de.marcschuler.onyxserver.repository.InviteRepository;
import de.marcschuler.onyxserver.service.CryptoService;
import de.marcschuler.onyxserver.service.ServerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the liquibase context "default-data" against an own empty in-memory database
 * and checks the seeded default server (changeSet 002-default-server)
 */
@SpringBootTest(properties = {
        "spring.liquibase.contexts=default-data",
        "spring.datasource.url=jdbc:h2:mem:default-server-seed"
})
@ActiveProfiles("test")
@Transactional
class DefaultServerSeedTest {

    @Autowired
    private ServerService serverService;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private CryptoService cryptoService;

    @Test
    void seedsDefaultServer() {
        var servers = serverService.all();
        assertEquals(1, servers.size());
        var server = servers.getFirst();

        assertEquals(DefaultServerSecretsChange.SERVER_ID, server.getId());
        assertEquals("Onyx Server", server.getName());
        assertNotNull(server.getKeys(), "keys generated");
        assertDoesNotThrow(() -> cryptoService.importKeyPair(server.getKeys().toJSONString()));
        assertEquals(3, server.getDescription().size());
        assertTrue(!server.getSettings().isNewUsersNeedInvite());

        assertEquals(List.of("Lobby", "Talk", "Chat"), server.getSections().stream().map(Section::getName).toList());
        assertEquals(List.of("Lobby", "Talking I", "Talking II", "Chatting", "Other", "Team"),
                server.getSections().stream().flatMap(s -> s.getChannels().stream()).map(Channel::getName).toList());
        server.getSections().stream().flatMap(s -> s.getChannels().stream())
                .forEach(c -> assertNotNull(c.getChat(), "chat for " + c.getName()));

        assertEquals(List.of("Admin", "Mod", "User"), server.getGroups().stream().map(Group::getName).sorted().toList());
        var user = server.getGroups().stream().filter(g -> g.getName().equals("User")).findFirst().orElseThrow();
        assertTrue(user.isDefaultForNewUsers());
        var teamRule = user.getPermissions().stream().filter(p -> p.isInverted()).findFirst().orElseThrow();
        assertEquals("Team", teamRule.getLimitedToChannel().iterator().next().getName());
    }

    @Test
    void seedsSingleUseAdminInvite() {
        var invites = inviteRepository.findAll();
        assertEquals(1, invites.size());
        var invite = invites.getFirst();

        assertEquals(16, invite.getCode().length());
        assertEquals(1, invite.getMaxUsages());
        assertEquals(0, invite.getUsages());
        assertTrue(invite.getEndDate().isAfter(LocalDateTime.now().plusDays(13)));
        assertEquals(DefaultServerSecretsChange.ADMIN_GROUP_ID, invite.getGroups().getFirst().getId());
    }
}
