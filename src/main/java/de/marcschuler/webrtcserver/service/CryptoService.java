package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.SignedContent;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
public class CryptoService {

    public <T> void validateMessage(SignedContent<T> content, PublicKey key){

    }
}
