package de.marcschuler.webrtcserver.error.webclient;

public class ProblemDetailException extends RuntimeException {
    public ProblemDetailException(String message) {
        super(message);
    }

    public ProblemDetailException(String message, Throwable cause) {
        super(message, cause);
    }
}
