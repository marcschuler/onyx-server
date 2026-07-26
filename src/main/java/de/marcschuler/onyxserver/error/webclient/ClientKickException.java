package de.marcschuler.onyxserver.error.webclient;

import de.marcschuler.onyxserver.webclient.KickReason;
import lombok.Getter;

public class ClientKickException extends RuntimeException {

    @Getter
    private final KickReason reason;

    public ClientKickException(String message, KickReason reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public ClientKickException(String message, KickReason reason) {
        super(message);
        this.reason = reason;
    }
}
