package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.data.ClientState;
import de.marcschuler.onyxserver.data.Group;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.client.ClientChangeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@OnyxTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private TestService testService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileService fileService;

    @MockitoBean
    private WebSocketConnectionService webSocketConnectionService;

    @Test
    void testUserRecognise() {
        var user = testService.userAdmin();
        assertEquals(user.getId(), userService.findById(user.getId()).orElseThrow().getId());
        assertEquals(user.getId(), userService.findById(cryptoService.generateKeyId(user.getPublicKey())).orElseThrow().getId());
    }

    @Test
    void testBanOfflineUser() {
        var user = testService.userUser();

        userService.ban(user, "Test ban");

        assertEquals(ClientState.BANNED, user.getState());
        verify(webSocketConnectionService, never()).kickClient(any(), any(), any());
    }

    @Test
    void testSetUserAvatar() throws Exception {
        var user = testService.userAdmin();
        var multipartFile = new MockMultipartFile(
                "file", "avatar.png", "image/png",
                new ByteArrayInputStream("fake-avatar-data".getBytes()));
        var file = fileService.uploadFile(multipartFile);

        userService.setUserAvatar(user, file);

        assertEquals(file.getId(), user.getAvatar().getId());
        verify(webSocketConnectionService).sendToAll(any(ClientChangeEvent.class));
    }

    @Test
    void testRegisterUserGetsDefaultGroup() {
        var keyPair = cryptoService.generateKeyPair();
        var user = userService.registerUser("newcomer", keyPair);

        assertNotNull(user.getId());
        assertEquals("newcomer", user.getUsername());
        assertEquals(ClientState.ACTIVE, user.getState());
        assertFalse(user.getGroups().isEmpty());
        assertTrue(user.getGroups().stream().anyMatch(Group::isDefaultForNewUsers));
    }
}
