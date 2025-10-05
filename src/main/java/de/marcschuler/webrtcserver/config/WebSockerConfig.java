package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
public class WebSockerConfig implements WebSocketConfigurer {

    private final WebSocketConnectionService webSocketConnectionService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketConnectionService, "/websocket")
                .setAllowedOrigins("*"); // TODO Adjust for production.
    }
}
