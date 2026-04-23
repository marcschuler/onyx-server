package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.dto.data.ChannelWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.ChannelRepository;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelChangeEvent;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelCreateEvent;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelDeleteEvent;
import de.marcschuler.webrtcserver.webclient.messages.channel.ChannelMoveEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final WebSocketConnectionService webSocketConnectionService;

    private final SectionRepository sectionRepository;
    private final ChannelRepository channelRepository;

    private final ServerMapper serverMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public Channel create(String name, Section section) {
        var channel = new Channel();
        channel.setName(name);
        channel.setChat(new Chat());
        section.getChannels().add(channel);
        channel = channelRepository.save(channel);
        sectionRepository.save(section);
        webSocketConnectionService.sendToAll(new ChannelCreateEvent(
                section.getId(), section.getChannels().indexOf(channel),
                serverMapper.mapToDTO(channel)
        ));
        log.info("Creating channel {} ({})", channel.getName(), channel.getId());
        return channel;
    }

    public Optional<Channel> get(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    public void order(Channel channel, int newOrder) {
        var section = channel.getSection();
        Util.reorder(section.getChannels(), channel, newOrder);
        sectionRepository.save(section);
        webSocketConnectionService.sendToAll(new ChannelMoveEvent(
                channel.getId(), newOrder, null
        ));
    }

    public void move(Channel channel, Section newSection, int newOrder) {
        if (channel.getSection().getId().equals(newSection.getId())) {
            log.warn("Tried to move channel {} ({}) to the same section {} ({})", channel.getName(), channel.getId(), newSection.getName(), newSection.getId());
            return;
        }
        var oldSection = channel.getSection();

        newSection.getChannels().add(newOrder, channel);
        sectionRepository.save(newSection);
        channelRepository.save(channel);

        oldSection.getChannels().remove(channel);
        sectionRepository.save(oldSection);

        webSocketConnectionService.sendToAll(
                new ChannelMoveEvent(channel.getId(), newOrder, newSection.getId())
        );
    }

    public void delete(Channel channel) {
        var section = channel.getSection();

        webSocketService.getClientsInChannel(channel)
                .forEach(webSocketConnectionService::leaveChannel);

        section.getChannels().removeIf(c -> c.equals(channel));
        sectionRepository.save(section);

        webSocketConnectionService.sendToAll(new ChannelDeleteEvent(channel.getId()));
        log.info("Removed channel {}:{}", channel.getId(), channel.getName());
    }

    public void edit(Channel channel, ChannelWriteDTO channelDTO) {
        serverMapper.update(channel, channelDTO);
        channelRepository.save(channel);
        webSocketConnectionService.sendToAll(new ChannelChangeEvent(serverMapper.mapToDTO(channel)));
    }
}
