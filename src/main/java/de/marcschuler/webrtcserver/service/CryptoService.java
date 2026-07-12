package de.marcschuler.webrtcserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import de.marcschuler.webrtcserver.dto.SignedContent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

    public static final String CRYPTO_ALGORITHM = "Ed25519";
    public static final String HASHING_ALGORITHM = "SHA-256";

    private final ObjectMapper objectMapper;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generated a secure random, 128 byte strong Base64 string
     *
     * @return
     */
    public String generateChallenge() {
        var bytes = new byte[128];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Generates a secure default KeyPair
     *
     * @return
     */
    @SneakyThrows
    public OctetKeyPair generateKeyPair() {
        log.info("Generating keypair");
        return new OctetKeyPairGenerator(Curve.Ed25519).generate();
    }

    /**
     * Generates the unique ID from a key
     *
     * @param publicKey
     * @return
     */
    @SneakyThrows
    public String generateKeyId(OctetKeyPair publicKey) {
        return publicKey.computeThumbprint("SHA-256").toString();
    }

    public OctetKeyPair importKeyPair(String data) throws ParseException {
        return OctetKeyPair.parse(data);
    }

    public OctetKeyPair importPublicKey(Map<String,Object> data) throws ParseException {
        return OctetKeyPair.parse(data);
    }

    public OctetKeyPair importPublicKey(String data) throws ParseException {
        return OctetKeyPair.parse(data);
    }


    /**
     * Signs content with the given key
     */
    public <T> SignedContent signContent(T content, OctetKeyPair privateKey) throws JacksonException, JOSEException {
        var contentString = objectMapper.writeValueAsBytes(content);
        var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.Ed25519)
                .type(JOSEObjectType.JOSE)
                .build();
        var payload = new Payload(contentString);
        var jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new Ed25519Signer(privateKey));
        return new SignedContent(jwsObject.serialize());
    }

    /**
     * Verifies a SignedContent
     *
     * @param content   the content with signature
     * @param key the key to verify against. Can be a public key only
     * @param <T>       the type of T.
     */
    public <T> T verifyContent(SignedContent content, Class<T> clazz, OctetKeyPair key) throws JacksonException, SignatureException, JOSEException, ParseException {
        if (content == null)
            throw new SignatureException("SignedContent is null");
        if (content.getJws()==null)
            throw new SignatureException("JWS is null");

        var jwsObject = JWSObject.parse(content.getJws());

        var verifier = new Ed25519Verifier(key.toPublicJWK());
        boolean isValid = jwsObject.verify(verifier);
        if (!isValid)
            throw new SignatureException("Invalid signature '" + content.getJws() + "' for content. The public key may not match or the content is not signed");

        return objectMapper.readValue(jwsObject.getPayload().toBytes(), clazz);
    }

    public <T> JsonNode verifyContent(SignedContent content, OctetKeyPair publicKey) throws JacksonException, SignatureException, JOSEException, ParseException {
        return verifyContent(content, JsonNode.class, publicKey);
    }

}
