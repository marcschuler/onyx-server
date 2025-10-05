package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final WebSocketService webSocketService;

    private final ChannelRepository channelRepository;

    @Transactional
    public Channel create(Channel channel) {
        Channel save = this.channelRepository.save(channel);
        webSocketService.updateServerTree();
        return save;
    }

    public Optional<Channel> get(UUID channelId) {
        return channelRepository.findById(channelId);
    }
}
