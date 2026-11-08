package de.marcschuler.onyxserver.error.webclient;

import de.marcschuler.onyxserver.webclient.KickReason;

/**
 * A client failed a security check, e.g. sent an invalid authentication proof.
 * The client is kicked with {@link KickReason#UNAUTHORIZED_REQUEST}. The message is sent to the client
 * and may be shown to the end user, so it should be understandable and not leak internals.
 */
public class ClientSecurityException extends ClientKickException {

    public ClientSecurityException(String message) {
        super(message, KickReason.UNAUTHORIZED_REQUEST);
    }

    public ClientSecurityException(String message, Throwable cause) {
        super(message, KickReason.UNAUTHORIZED_REQUEST, cause);
    }
}
