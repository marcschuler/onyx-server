package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.error.webclient.PermissionDeniedException;
import de.marcschuler.onyxserver.webclient.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

}