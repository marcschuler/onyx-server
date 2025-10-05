package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.SectionDTO;
import de.marcschuler.webrtcserver.dto.SectionWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v0/server/{serverId}/section")
@RequiredArgsConstructor
public class SectionController {

    private final ServerService serverService;
    private final SectionService sectionService;

    private final ServerMapper serverMapper;

    @PostMapping
    public SectionDTO create(@RequestBody SectionWriteDTO sectionDto, @PathVariable UUID serverId) {
        var server = serverService.get(serverId).orElseThrow();
        var section = serverMapper.mapFromDTO(sectionDto);
        section.setServer(server);
        section = sectionService.create(section);
        return serverMapper.mapToDTO(section);
    }

    @PutMapping("{sectionId}")
    public SectionDTO edit(@RequestBody SectionWriteDTO sectionDto,@PathVariable UUID sectionId) {
        var section = sectionService.findById(sectionId).orElseThrow();
        serverMapper.update(section,sectionDto);
        sectionService.save(section);
        return serverMapper.mapToDTO(section);
    }

    @PutMapping("{sectionId}/reorder/{newOrder}")
    public void reorder(@PathVariable UUID sectionId, @PathVariable int newOrder) {
        var section = sectionService.findById(sectionId).orElseThrow();
        sectionService.reorder(section, newOrder);
    }

    @DeleteMapping("{sectionId}")
    public void delete(@PathVariable UUID sectionId, @PathVariable UUID serverId) {
        sectionService.delete(sectionId);
    }
}
