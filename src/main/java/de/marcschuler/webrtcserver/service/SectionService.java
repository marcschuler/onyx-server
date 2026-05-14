package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.repository.ServerRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionChangeEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionCreateEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionDeleteEvent;
import de.marcschuler.webrtcserver.webclient.messages.section.SectionMoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {
    private final WebSocketConnectionService webSocketConnectionService;
    private final WebSocketService webSocketService;

    private final SectionRepository sectionRepository;

    private final ServerMapper serverMapper;
    private final ServerRepository serverRepository;

    public Optional<Section> findById(UUID id) {
        return sectionRepository.findById(id);
    }

    public Section create(Server server, SectionDTO sectionWriteDTO) {
        var section = serverMapper.mapFromDTO(sectionWriteDTO);
        server.getSections().add(section);
        serverRepository.save(server);

        webSocketConnectionService.sendToAll(
                new SectionCreateEvent(serverMapper.mapToDTO(section),
                        server.getSections().indexOf(section))
        );

        return section;
    }

    public void delete(Section section) {
        log.info("Deleting section {}", section.getId());
        section.getServer().getSections().removeIf(s -> s == section);
        serverRepository.save(section.getServer());

        webSocketConnectionService.sendToAll(new SectionDeleteEvent(section.getId()));
    }

    public void order(Section section, int newOrder) {
        var server = section.getServer();
        Util.reorder(server.getSections(), section, newOrder);
        serverRepository.save(server);
        webSocketConnectionService.sendToAll(
                new SectionMoveEvent(section.getId(),newOrder)
        );
    }


    public void update(Section section, SectionDTO sectionDto) {
        log.info("Updating section {}", section.getName());
        serverMapper.update(section, sectionDto);
        this.sectionRepository.save(section);
        webSocketConnectionService.sendToAll(new SectionChangeEvent(
                serverMapper.mapToDTO(section)
        ));
    }

    public Optional<Section> get(UUID id) {
        return sectionRepository.findById(id);
    }
}
