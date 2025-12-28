package de.marcschuler.webrtcserver.error.webclient;

public class NoClientException extends RuntimeException {
    public NoClientException(String message) {
        super(message);
    }
}
