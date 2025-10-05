package de.marcschuler.webrtcserver.webclient.handler;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.error.webclient.NotFoundException;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.webclient.WebClientConnectionService;
import de.marcschuler.webrtcserver.service.webclient.WebClientDataService;
import de.marcschuler.webrtcserver.webclient.events.ClientEvent;
import de.marcschuler.webrtcserver.webclient.events.config.ChannelCreateRequest;
import de.marcschuler.webrtcserver.webclient.events.peer.PeerOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientServerConfigHandler {

    private final WebClientConnectionService webClientConnectionService;
    private final WebClientDataService webClientDataService;

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

        webClientDataService.updateServerTree();
    }
}
