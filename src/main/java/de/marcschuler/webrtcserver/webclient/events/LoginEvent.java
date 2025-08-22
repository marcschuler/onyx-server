package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

@Data
public class LoginEvent extends Event {
    private String username;
}
