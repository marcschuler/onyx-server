package de.marcschuler.webrtcserver.error.webclient;

public class PolicyCheckException extends Throwable{
    public PolicyCheckException() {
    }

    public PolicyCheckException(String message) {
        super(message);
    }

    public PolicyCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
