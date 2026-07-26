package de.marcschuler.onyxserver.service.gameintegration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum ClientGameIntegrationState {
    PROFILE_NOT_FOUND(Duration.ofDays(1)),
    PROFILE_INACESSIBLE(Duration.ofMinutes(5)),
    PROFILE_OFFLINE(Duration.ofSeconds(30));

    private final Duration retryDelay;
}
