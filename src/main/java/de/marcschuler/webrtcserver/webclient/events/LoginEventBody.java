package de.marcschuler.webrtcserver.webclient.events;

import lombok.Data;

@Data
public class LoginEventBody extends EventBody {
    private String username;
}
