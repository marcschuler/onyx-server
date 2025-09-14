package de.marcschuler.webrtcserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testKeyId() throws IOException, ParseException, InvalidKeySpecException, JOSEException {

        ClassPathResource resource = new ClassPathResource("crypto/publickey-1.txt");
        String content = Files.readString(resource.getFile().toPath());
        var key = cryptoService.parsePublicKey(JWK.parse(content));
        assertEquals("i2lMeB/Sw94WvkLiAccs9/HE7g2RMazoqKl0hqSeW+k=", cryptoService.generateKeyId(key));
    }

    @Test
    void testJWKExportImport() throws JsonProcessingException, ParseException, InvalidKeySpecException, JOSEException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        var keyPair = cryptoService.generateKeyPair();
        var publicJWK = cryptoService.exportPublicKeyToJSON(keyPair.getPublic()).toString();
        var importedKey = cryptoService.parsePublicKey(JWK.parse(publicJWK));


        var content = cryptoService.signContent("Test String", keyPair.getPrivate());

        //Normal and exported+imported public key should behave equally
        cryptoService.verifyContent(content,keyPair.getPublic());
        cryptoService.verifyContent(content,importedKey);

    }
}