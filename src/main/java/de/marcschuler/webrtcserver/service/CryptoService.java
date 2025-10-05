package de.marcschuler.webrtcserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import de.marcschuler.webrtcserver.dto.SignedContent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

    public static final String CRYPTO_ALGORITHM = "Ed25519";
    public static final String HASHING_ALGORITHM = "SHA-256";

    private final ObjectMapper objectMapper;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateChallenge() {
        var bytes = new byte[128];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @SneakyThrows
    public KeyPair generateKeyPair() {
        log.info("Generating keypair");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM);
        return kpg.generateKeyPair();
    }

    @SneakyThrows
    public String generateKeyId(PublicKey publicKey) {
        MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);
        byte[] encodedhash = digest.digest(publicKey.getEncoded());
        return Base64.getEncoder().encodeToString(encodedhash);
    }

    public JWK exportPublicKey(PublicKey publicKey) {
        return new OctetKeyPair.Builder((OctetKeyPair) publicKey)
                .keyID(generateKeyId(publicKey))
                .build();
    }

    public JsonNode exportPublicKeyToJSON(PublicKey publicKey) throws JsonProcessingException {
        var keyBytes = publicKey.getEncoded();
        // Extract raw 32-byte key (skip first 12 bytes of X.509 header)
        // X.509 Ed25519 public keys have 12-byte prefix, adjust if needed
        byte[] rawKey = new byte[32];
        System.arraycopy(keyBytes, keyBytes.length - 32, rawKey, 0, 32);

        // Create JWK
        OctetKeyPair jwk = new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(rawKey))
                .keyID(generateKeyId(publicKey))
                .build();

        return objectMapper.readTree(jwk.toJSONString());
    }

    public PublicKey parsePublicKey(JWK jwk) throws InvalidKeySpecException, JOSEException {
        if (jwk instanceof OctetKeyPair okp && okp.getCurve().equals(Curve.Ed25519)) {
            // okp.toPublicKey(); - somehow jose can not convert to ed25519 (old java requirements???)
            byte[] rawKey = Base64.getUrlDecoder().decode(okp.getX().toString());

            // ASN.1 DER prefix for Ed25519 SPKI
            byte[] prefix = new byte[]{
                    0x30, 0x2a,                     // SEQUENCE (length 42)
                    0x30, 0x05,                     // SEQUENCE (length 5)
                    0x06, 0x03, 0x2b, 0x65, 0x70,   // OID 1.3.101.112 (Ed25519)
                    0x03, 0x21, 0x00                // BIT STRING (length 33, 0 unused bits)
            };

            byte[] spki = new byte[prefix.length + rawKey.length];
            System.arraycopy(prefix, 0, spki, 0, prefix.length);
            System.arraycopy(rawKey, 0, spki, prefix.length, rawKey.length);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(spki);
            KeyFactory kf = null;
            try {
                kf = KeyFactory.getInstance(CRYPTO_ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Cannot get crypto algorithm", e);
            }
            return kf.generatePublic(spec);
        }
        throw new InvalidKeySpecException("Not an valid Ed25519 key");
    }

    public PublicKey parsePublicKey(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Signs content with the given key
     */
    public <T> SignedContent<T> signContent(T content, PrivateKey privateKey) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        var signature = Signature.getInstance(CRYPTO_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(objectMapper.writeValueAsBytes(content));
        var signatureBase64 = Base64.getEncoder().encodeToString(signature.sign());
        var jsonString = objectMapper.writeValueAsString(content);
        return new SignedContent<>(jsonString, signatureBase64);
    }

    /**
     * Verifies a SignedContent
     *
     * @param content   the content with signature
     * @param publicKey the public key to verify against
     * @param <T>       the type of T.
     */
    public <T> T verifyContent(SignedContent<T> content, Class<T> clazz, PublicKey publicKey) throws InvalidKeyException, JsonProcessingException, SignatureException, NoSuchAlgorithmException {
        var signature = Signature.getInstance(CRYPTO_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(content.getContent().getBytes(StandardCharsets.UTF_8));
        if (!signature.verify(Base64.getDecoder().decode(content.getContentSignature())))
            throw new SignatureException("Invalid signature '" + content.getContentSignature() + "' for content. The public key may not match or the content is not signed");
        return objectMapper.readValue(content.getContent(), clazz);
    }

    public <T> JsonNode verifyContent(SignedContent<T> content, PublicKey publicKey) throws InvalidKeyException, JsonProcessingException, SignatureException, NoSuchAlgorithmException {
        return verifyContent((SignedContent<JsonNode>) content,JsonNode.class, publicKey);
    }

}
