package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.error.webclient.NotFoundException;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.config.ChannelCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerConfigHandler {

    private final WebSocketConnectionService webSocketConnectionService;
    private final WebSocketService webSocketService;

    private final ChannelService channelService;
    private final SectionService sectionService;

    @EventListener
    public void onOffer(ClientEvent<ChannelCreateRequest> event) throws NotFoundException {
        var section = sectionService.findById(event.getBody().getSection())
                .orElseThrow(()-> new NotFoundException("Section " + event.getBody().getSection() + " does not exist"));
        var channel = new Channel();
        channel.setName(event.getBody().getName());
        channel.setSection(section);
        channelService.create(channel);

        webSocketService.updateServerTree();
    }
}
