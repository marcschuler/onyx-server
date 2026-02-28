package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import tools.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;


@OnyxTest
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
    void testValidation() throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, JacksonException, JOSEException, ParseException {
        var pair = cryptoService.generateKeyPair();
        var content = cryptoService.signContent("Test String", pair);
        log.info(objectMapper.writeValueAsString(content));

        var publicKeyPair = cryptoService.importPublicKey(pair.toPublicJWK().toJSONObject());
        cryptoService.verifyContent(content, publicKeyPair);
    }

    @Test
    void testKeyId() throws IOException, ParseException, InvalidKeySpecException, JOSEException {

        ClassPathResource resource = new ClassPathResource("crypto/publickey-1.txt");
        String content = Files.readString(resource.getFile().toPath());
        var key = cryptoService.importPublicKey(content);
        assertEquals("gF7RbPyj2RmmvKnh3B6KO0anmcJ_dovrcqZ5z45-cv4", cryptoService.generateKeyId(key));
    }


    @Test
    void testThumbprint() throws ParseException {
        var keyPair = cryptoService.generateKeyPair();

        assertEquals(cryptoService.generateKeyId(keyPair), cryptoService.generateKeyId(keyPair.toPublicJWK()));
        assertEquals(cryptoService.generateKeyId(keyPair), cryptoService.generateKeyId(cryptoService.importKeyPair(keyPair.toJSONString())));
    }

    @Test
    void testJWKExportImport() throws JacksonException, ParseException, InvalidKeySpecException, JOSEException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        var keyPair = cryptoService.generateKeyPair();
        var publicJWK = keyPair.toPublicJWK().toJSONString();
        var importedKey = cryptoService.importPublicKey(publicJWK);


        var content = cryptoService.signContent("Test String", keyPair);

        //Normal and exported+imported public key should behave equally
        cryptoService.verifyContent(content,keyPair);
        cryptoService.verifyContent(content,importedKey);

    }
}