package de.marcschuler.webrtcserver.webclient;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class ClientMessage<T extends MessageBody> implements ResolvableTypeProvider {
    private final T body;
    private final WebClient client;
    private final LocalDateTime received;

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(), ResolvableType.forInstance(getBody())
        );
    }
}
