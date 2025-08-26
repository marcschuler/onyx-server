package de.marcschuler.webrtcserver.webclient.events;

import de.marcschuler.webrtcserver.webclient.WebClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@RequiredArgsConstructor
@Data
public class ClientEvent <T extends EventBody> implements ResolvableTypeProvider {
    private final T body;
    private final WebClient client;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(), ResolvableType.forInstance(getBody())
        );
    }
}
