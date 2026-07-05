package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@OnyxTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void testNoAvatar() {
        var ex = assertThrows(ResponseStatusException.class, () ->
                userController.avatar(TestService.USER_ADMIN_ID));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

}