package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testJWT(){
        var user = new User();
        user.setId("test");
        var jwt = authService.createJWT(user);
        assertNotNull(jwt);

        var userId = authService.verifyJWT(jwt);
        assertEquals("test",userId);
    }

}