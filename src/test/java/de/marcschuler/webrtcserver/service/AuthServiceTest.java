package de.marcschuler.webrtcserver.service;

import com.nimbusds.jose.JOSEException;
import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@OnyxTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testJWT() throws JOSEException, ParseException {
        var user = new User();
        user.setId("test");
        var jwt = authService.createJWT(user);
        assertNotNull(jwt);

        var userId = authService.verifyJWT(jwt);
        assertEquals("test",userId);
    }

}