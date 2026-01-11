package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.dto.data.SectionWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/section", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SectionController implements OrderableController {

    private final ServerService serverService;
    private final SectionService sectionService;

    private final ServerMapper serverMapper;

    @PostMapping
    public SectionDTO create(@RequestBody SectionWriteDTO sectionDto) {
        var server = serverService.defaultServer();
        return serverMapper.mapToDTO(sectionService.create(server,sectionDto));
    }

    @PutMapping("{sectionId}")
    public SectionDTO edit(@RequestBody SectionWriteDTO sectionDto, @PathVariable UUID sectionId) {
        var section = sectionService.findById(sectionId).orElseThrow();
        sectionService.update(section, sectionDto);
        return serverMapper.mapToDTO(section);
    }

    @Override
    public void order(UUID id, int newOrder) {
        var channel = sectionService.get(id).orElseThrow();
        sectionService.order(channel, newOrder);
    }

    @DeleteMapping("{sectionId}")
    public void delete(@PathVariable UUID sectionId) {
        sectionService.delete(sectionId);
    }


}
