package de.marcschuler.onyxserver.webclient;

import de.marcschuler.onyxserver.webclient.messages.MessageBody;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.LocalDateTime;

public record ClientMessage<T extends MessageBody>(T body, WebClient client,
                                                   LocalDateTime received) implements ResolvableTypeProvider {
    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(), ResolvableType.forInstance(body())
        );
    }
}
