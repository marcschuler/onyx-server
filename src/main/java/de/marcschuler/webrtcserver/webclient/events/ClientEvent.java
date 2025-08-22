package de.marcschuler.webrtcserver.webclient.events;

import de.marcschuler.webrtcserver.service.WebSocketSignalingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@RequiredArgsConstructor
@Data
public class ClientEvent <T extends Event> implements ResolvableTypeProvider {
    private final T event;
    private final WebSocketSignalingService.WebClient client;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(), ResolvableType.forInstance(getEvent())
        );
    }
}
