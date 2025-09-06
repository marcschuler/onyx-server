package de.marcschuler.webrtcserver.webclient.events.auth;

import de.marcschuler.webrtcserver.webclient.events.EventBody;
import jakarta.annotation.Nullable;

public class AuthSuccessEvent extends EventBody {
    @Nullable
    private String message;
}
