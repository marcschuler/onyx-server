package de.marcschuler.webrtcserver.error;

public class FilePreviewException extends RuntimeException {
    public FilePreviewException(String message) {
        super(message);
    }

    public FilePreviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
