package de.marcschuler.onyxserver.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChatDisabledException extends ResponseStatusException {
    public ChatDisabledException() {
        super(HttpStatus.FORBIDDEN, "Chat is disabled or archived. New messages cannot be creatd");
    }
}
