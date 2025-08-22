package de.marcschuler.webrtcserver.webclient.error;

public class ClientSecurityViolationException extends Throwable{
    public ClientSecurityViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientSecurityViolationException(String message) {
        super(message);
    }
}
