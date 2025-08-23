package de.marcschuler.webrtcserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcschuler.webrtcserver.dto.SignedContent;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;

@Service
public class CryptoService {

    public static final String CRYPTO_ALGORITHM = "Ed25519";
    public static final String HASHING_ALGORITHM = "SHA-256";

    //TODO sort by keys to avoid errors verifiying and signing
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateChallenge(){
        var bytes = new byte[128];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM);
        return kpg.generateKeyPair();
    }

    @SneakyThrows
    public String generateKeyId(PublicKey publicKey) {
        MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);
        byte[] encodedhash = digest.digest(publicKey.getEncoded());
        return Base64.getEncoder().encodeToString(encodedhash);
    }

    /**
     * Signs content with the given key
     */
    public <T> SignedContent<T> signContent(T content, PrivateKey privateKey) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        var signature = Signature.getInstance(CRYPTO_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(objectMapper.writeValueAsBytes(content));
        var signatureBase64 = Base64.getEncoder().encodeToString(signature.sign());
        return new SignedContent<>(content, signatureBase64);
    }

    /**
     * Verifies a SignedContent
     *
     * @param content   the content with signature
     * @param publicKey the public key to verify against
     * @param <T>       the type of T.
     */
    public <T> void verifyContent(SignedContent<T> content, PublicKey publicKey) throws InvalidKeyException, JsonProcessingException, SignatureException, NoSuchAlgorithmException {
        var signature = Signature.getInstance(CRYPTO_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(objectMapper.writeValueAsBytes(content.getContent()));
        if (!signature.verify(Base64.getDecoder().decode(content.getContentSignature())))
            throw new SignatureException("Invalid signature '" + content.getContentSignature() + "' for content. The public key may not match or the content is not signed");
    }

}
