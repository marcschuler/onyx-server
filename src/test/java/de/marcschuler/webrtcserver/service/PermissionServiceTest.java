package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.error.webclient.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
           permissionService.checkAccess(new User(),null,null,PermissionType.CHANNEL);
        });
    }

    @Test
    void testNoPermissionInChannel() {
        assertThrows(PermissionDeniedException.class, () -> {
            permissionService.checkAccess(new User(),testService.sectionLobby(),testService.channelLobby(),PermissionType.CHANNEL);
        });
    }

    @Test
    void testAdminGroupContext() {
        var result = permissionService.buildGroupContext(testService.userAdmin(), null, null);
        assertEquals(List.of(testService.groupAdmin()), result);
    }

    @Test
    void testAdminAccess() {
        permissionService.checkAccess(testService.userAdmin(), null, null, PermissionType.CHANNEL_DELETE);
    }

}