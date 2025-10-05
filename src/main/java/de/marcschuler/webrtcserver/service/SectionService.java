package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.Util;
import de.marcschuler.webrtcserver.data.Section;
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
public class SectionService {

    private final SectionRepository sectionRepository;

    private final WebSocketService webSocketService;

    public Optional<Section> findById(UUID id) {
        return sectionRepository.findById(id);
    }

    public Section create(Section section) {
        section = sectionRepository.save(section);
        webSocketService.updateServerTree();
        return section;
    }

    public void delete(UUID sectionId) {
        this.sectionRepository.deleteById(sectionId);
        webSocketService.updateServerTree();
    }

    public void reorder(Section section,int newOrder) {
        var sections = section.getServer().getSections();
        Util.reorder(sections, sections.indexOf(section), newOrder);
        webSocketService.updateServerTree();
    }

    public void save(Section section) {
        this.sectionRepository.save(section);
        webSocketService.updateServerTree();
    }
}
