package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.dto.data.ChannelWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
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

    private final SectionRepository sectionRepository;
    private final ChannelRepository channelRepository;

    private final ServerMapper serverMapper;

    public Channel create(String name, Section section) {
        var channel = new Channel();
        channel.setName(name);
        channel.setChat(new Chat());
        section.getChannels().add(channel);
        sectionRepository.save(section);
        webSocketService.updateServerTree();
        return channelRepository.save(channel);
    }

    public Optional<Channel> get(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    public void order(Channel channel, int newOrder) {
        var section = channel.getSection();
        Util.reorder(section.getChannels(), channel, newOrder);
        sectionRepository.save(section);
        webSocketService.updateServerTree();
    }

    public void move(Channel channel, Section newSection, int newOrder) {
        var section = channel.getSection();
        section.getChannels().removeIf(c -> c == channel);
        newSection.getChannels().add(channel);
        channel.setSection(newSection);

        sectionRepository.save(section);
        sectionRepository.save(newSection);

        order(channel, newOrder);
    }

    public void delete(Channel channel) {
        channelRepository.delete(channel);
        webSocketService.updateServerTree();
    }

    public void edit(Channel channel, ChannelWriteDTO channelDTO) {
        serverMapper.update(channel, channelDTO);
        channelRepository.save(channel);
        webSocketService.updateServerTree();
    }
}
