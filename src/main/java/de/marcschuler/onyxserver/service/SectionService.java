package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.Util;
import de.marcschuler.onyxserver.data.Section;
import de.marcschuler.onyxserver.data.Server;
import de.marcschuler.onyxserver.dto.SectionCreateDTO;
import de.marcschuler.onyxserver.dto.data.SectionDTO;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.repository.SectionRepository;
import de.marcschuler.onyxserver.repository.ServerRepository;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.service.websocket.WebSocketService;
import de.marcschuler.onyxserver.webclient.messages.section.SectionChangeEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionCreateEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionDeleteEvent;
import de.marcschuler.onyxserver.webclient.messages.section.SectionMoveEvent;
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

    public Section create(Server server, SectionCreateDTO sectionCreateDTO) {
        var section = serverMapper.mapFromDTO(sectionCreateDTO);
        server.getSections().add(section);
        serverRepository.save(server);

        webSocketConnectionService.sendToAll(
                new SectionCreateEvent(serverMapper.mapToDTOExtended(section),
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
