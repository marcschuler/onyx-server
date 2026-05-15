package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.GroupCreateDTO;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.mapper.GroupMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/group",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private final GroupMapper groupMapper;

    @GetMapping
    public List<GroupDTO> all() {
        return groupService.all().stream()
                .map(groupMapper::mapToDTO)
                .toList();
    }

    @PostMapping
    public GroupDTO create(@RequestBody GroupCreateDTO groupCreateDTO) {
        var group = groupService.create(groupCreateDTO);
        return groupMapper.mapToDTO(group);
    }

    @PutMapping("{id}")
    public GroupDTO edit(@PathVariable UUID id, @RequestBody GroupDTO groupWriteDTO) {
        var group = groupService.get(id).orElseThrow();
        groupService.edit(group, groupWriteDTO);
        return groupMapper.mapToDTO(group);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        var group = groupService.get(id).orElseThrow();
        groupService.delete(group);
    }
}
