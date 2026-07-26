package de.marcschuler.onyxserver.webclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WebClientState {
    NOT_AUTHORIZED(false),
    LOGGED_IN(true), INVALID(false);

    private final boolean interactionAllowed;


}
