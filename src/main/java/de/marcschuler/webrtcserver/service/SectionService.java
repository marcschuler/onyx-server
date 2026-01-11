package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.Server;
import de.marcschuler.webrtcserver.dto.data.SectionWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.SectionRepository;
import de.marcschuler.webrtcserver.repository.ServerRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {
    private final WebSocketService webSocketService;

    private final SectionRepository sectionRepository;

    private final ServerMapper serverMapper;
    private final ServerRepository serverRepository;

    public Optional<Section> findById(UUID id) {
        return sectionRepository.findById(id);
    }

    public Section create(Server server, SectionWriteDTO sectionWriteDTO) {
        var section = serverMapper.mapFromDTO(sectionWriteDTO);
        server.getSections().add(section);
        serverRepository.save(server);
        webSocketService.updateServerTree();
        return section;
    }

    public void delete(Section section) {
        log.info("Deleting section {}", section.getId());
        section.getServer().getSections().removeIf(s -> s == section);
        serverRepository.save(section.getServer());
        webSocketService.updateServerTree();
    }

    public void order(Section section, int newOrder) {
        var server = section.getServer();
        Util.reorder(server.getSections(), section, newOrder);
        serverRepository.save(server);
        webSocketService.updateServerTree();
    }

    private void saveChanges(Section section) {
        this.sectionRepository.save(section);
        webSocketService.updateServerTree();
    }

    public void update(Section section, SectionWriteDTO sectionDto) {
        log.info("Updating section {}", section.getName());
        serverMapper.update(section, sectionDto);
        saveChanges(section);
    }

    public Optional<Section> get(UUID id) {
        return sectionRepository.findById(id);
    }
}
