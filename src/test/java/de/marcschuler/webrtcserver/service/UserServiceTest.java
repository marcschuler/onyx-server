package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private TestService testService;

    @Test
    void testUserRecognise(){
        var user = testService.userAdmin();
        assertEquals(user,userService.findById(user.getId()).orElseThrow());
        assertEquals(user,userService.findById(cryptoService.generateKeyId(user.getPublicKey())).orElseThrow());
    }

}