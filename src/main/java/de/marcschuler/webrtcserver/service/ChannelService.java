package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;

    @Transactional
    public Channel create(Channel channel) {
        return this.channelRepository.save(channel);
    }
}
