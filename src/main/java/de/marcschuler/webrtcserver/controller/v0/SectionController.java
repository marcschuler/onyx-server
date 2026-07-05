package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.dto.SectionCreateDTO;
import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.PermissionService;
import de.marcschuler.webrtcserver.service.SectionService;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/section", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SectionController implements OrderableController {

    private final ServerService serverService;
    private final SectionService sectionService;
    private final PermissionService permissionService;

    private final ServerMapper serverMapper;

    @GetMapping("{id}")
    public SectionDTO section(@PathVariable UUID id) {
        return sectionService.get(id).map(serverMapper::mapToDTO)
                .orElseThrow();
    }

    @PostMapping
    public SectionDTO create(@RequestBody SectionCreateDTO sectionCreateDTO) {
        permissionService.checkControllerAccess(null,null, PermissionType.SERVER_SECTION_CREATE);

        var server = serverService.defaultServer();
        return serverMapper.mapToDTO(sectionService.create(server, sectionCreateDTO));
    }

    @PutMapping("{sectionId}")
    public SectionDTO edit(@RequestBody SectionDTO sectionDto, @PathVariable UUID sectionId) {
        var section = sectionService.findById(sectionId).orElseThrow();
        permissionService.checkControllerAccess(section,null, PermissionType.SECTION_EDIT);
        sectionService.update(section, sectionDto);
        return serverMapper.mapToDTO(section);
    }

    @Override
    public void order(UUID id, int newOrder) {
        permissionService.checkControllerAccess(null,null, PermissionType.SERVER_SECTION_MOVE);

        var section = sectionService.get(id).orElseThrow();
        sectionService.order(section, newOrder);
    }

    @DeleteMapping("{sectionId}")
    public void delete(@PathVariable UUID sectionId) {
        var section = sectionService.get(sectionId).orElseThrow();

        permissionService.checkControllerAccess(section,null, PermissionType.SECTION_DELETE);

        sectionService.delete(section);
    }


}
