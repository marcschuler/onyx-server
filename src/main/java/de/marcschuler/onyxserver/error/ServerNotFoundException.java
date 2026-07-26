package de.marcschuler.onyxserver.error;

import java.util.UUID;

public class ServerNotFoundException extends RuntimeException {
    public ServerNotFoundException(UUID message) {
        super("Could not find server '" + message.toString() + "'");
    }
}
