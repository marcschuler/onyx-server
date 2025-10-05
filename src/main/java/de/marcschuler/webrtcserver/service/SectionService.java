package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Section;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionService sectionService;

    public Optional<Section> findById(UUID id) {
        return sectionService.findById(id);
    }
}
