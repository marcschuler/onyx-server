package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.data.Group;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.data.permission.Permission;
import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.error.webclient.PermissionDeniedException;
import de.marcschuler.onyxserver.webclient.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@OnyxTest
@Slf4j
class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TestService testService;

    @Test
    void testNoPermission() {
        assertThrows(PermissionDeniedException.class, () -> {
            var client = new WebClient(null);
            client.setUser(new User());
            permissionService.checkClientAccess(client, null, null, PermissionType.CHANNEL);
        });
    }

    @Test
    void testNoPermissionInChannel() {
        assertThrows(PermissionDeniedException.class, () -> {
            var client = new WebClient(null);
            client.setUser(new User());
            permissionService.checkClientAccess(client, testService.sectionLobby(), testService.channelLobby(), PermissionType.CHANNEL);
        });
    }

    @Test
    void testAdminGroupContext() {
        var result = permissionService.buildGroupContext(testService.userAdmin(), null, null);
        assertEquals(1,result.size());
        assertEquals(testService.groupAdmin().getId(), result.iterator().next().getId());
    }

    @Test
    void testAdminAccess() {
        var client = new WebClient(null);
        client.setUser(testService.userAdmin());
        permissionService.checkClientAccess(client, null, null, PermissionType.CHANNEL_DELETE);
    }

    @Test
    void testBannedUserDenied() {
        var user = testService.userAdmin();
        user.setState(ClientState.BANNED);

        var client = new WebClient(null);
        client.setUser(user);

        assertThrows(PermissionDeniedException.class, () ->
                permissionService.checkClientAccess(client, null, null, PermissionType.CHANNEL));
    }

    @Test
    void testPendingUserDenied() {
        var user = testService.userAdmin();
        user.setState(ClientState.PENDING_ACCESS);

        var client = new WebClient(null);
        client.setUser(user);

        assertThrows(PermissionDeniedException.class, () ->
                permissionService.checkClientAccess(client, null, null, PermissionType.CHANNEL));
    }

    @Test
    void testInvertedPermissionDenies() {
        var user = new User();
        user.setState(ClientState.ACTIVE);

        var group = new Group();
        group.setPriority(0);

        var permission = new Permission();
        permission.setInverted(true);
        permission.setPermissions(Set.of(PermissionType.CHANNEL));
        group.setPermissions(List.of(permission));
        user.setGroups(Set.of(group));

        assertThrows(PermissionDeniedException.class, () ->
                permissionService.checkAccess(user, null, null, PermissionType.CHANNEL));
    }

    @Test
    void testLimitedToSection() {
        var user = new User();
        user.setState(ClientState.ACTIVE);

        var group = new Group();
        group.setPriority(0);

        var lobbySection = testService.sectionLobby();
        var permission = new Permission();
        permission.setInverted(false);
        permission.setPermissions(Set.of(PermissionType.CHANNEL));
        permission.setLimitedToSection(Set.of(lobbySection));
        group.setPermissions(List.of(permission));
        user.setGroups(Set.of(group));

        permissionService.checkAccess(user, lobbySection, testService.channelLobby(), PermissionType.CHANNEL);

        var talkSection = testService.sectionTalk();
        assertThrows(PermissionDeniedException.class, () ->
                permissionService.checkAccess(user, talkSection, testService.sectionTalk().getChannels().getFirst(), PermissionType.CHANNEL));
    }

    @Test
    void testPriorityOrdering() {
        var user = new User();
        user.setState(ClientState.ACTIVE);

        var denyGroup = new Group();
        denyGroup.setPriority(0);
        var denyPermission = new Permission();
        denyPermission.setInverted(true);
        denyPermission.setPermissions(Set.of(PermissionType.CHANNEL));
        denyGroup.setPermissions(List.of(denyPermission));

        var allowGroup = new Group();
        allowGroup.setPriority(100);
        var allowPermission = new Permission();
        allowPermission.setInverted(false);
        allowPermission.setPermissions(Set.of(PermissionType.CHANNEL));
        allowGroup.setPermissions(List.of(allowPermission));

        user.setGroups(new HashSet<>(List.of(allowGroup, denyGroup)));

        permissionService.checkAccess(user, null, null, PermissionType.CHANNEL);
    }

}