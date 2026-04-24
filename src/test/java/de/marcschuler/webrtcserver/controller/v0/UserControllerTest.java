package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void testNoAvatar() {
        var ex = assertThrows(ResponseStatusException.class, () ->
                userController.avatar(TestConstants.USER_ADMIN_MARC_ID));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

}