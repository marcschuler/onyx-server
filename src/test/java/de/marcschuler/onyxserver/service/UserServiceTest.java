package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(user.getId(),userService.findById(user.getId()).orElseThrow().getId());
        assertEquals(user.getId(),userService.findById(cryptoService.generateKeyId(user.getPublicKey())).orElseThrow().getId());
    }

}