package de.marcschuler.onyxserver.service.websocket;

import de.marcschuler.onyxserver.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketConnectionServicePreliminaryConnectionTest {

    @Test
    void cleanPreliminaryConnectionsClosesAndRemovesExpiredConnections() throws Exception {
        var service = new WebSocketConnectionService(
                mock(ApplicationEventPublisher.class),
                mock(AuthService.class),
                mock(ObjectMapper.class)
        );
        ReflectionTestUtils.setField(service, "expireLogin", Duration.ZERO);

        var session = mock(WebSocketSession.class);
        service.afterConnectionEstablished(session);

        @SuppressWarnings("unchecked")
        var connections = (List<Object>) ReflectionTestUtils.getField(service, "preliminaryConnections");
        assertEquals(1, connections.size());

        service.cleanPreliminaryConnections();

        assertTrue(connections.isEmpty());
        verify(session).close();
    }
}
