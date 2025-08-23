package de.marcschuler.webrtcserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@SpringBootTest
@Slf4j
class CryptoServiceTest {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testKeyGeneration() {
        cryptoService.generateKeyPair();
    }

    @Test
    void testValidation() throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        var pair = cryptoService.generateKeyPair();
        var content = cryptoService.signContent("Test String", pair.getPrivate());
        log.info(objectMapper.writeValueAsString(content));
        cryptoService.verifyContent(content, pair.getPublic());
    }
}