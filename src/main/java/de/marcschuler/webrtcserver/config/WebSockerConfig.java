package de.marcschuler.webrtcserver.config;

import de.marcschuler.webrtcserver.service.WebSocketSignalingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
public class WebSockerConfig implements WebSocketConfigurer {

    private final WebSocketSignalingService webSocketSignalingService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketSignalingService, "/websocket")
                .setAllowedOrigins("*"); // Adjust for production.
    }
}
