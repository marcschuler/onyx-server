package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.error.webclient.PermissionDeniedException;
import de.marcschuler.webrtcserver.webclient.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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