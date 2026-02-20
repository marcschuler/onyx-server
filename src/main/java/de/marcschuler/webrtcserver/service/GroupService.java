package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.dto.data.GroupWriteDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;

    private final ServerMapper serverMapper;

    public Group create(GroupWriteDTO policyWriteDTO) {
        var group = serverMapper.mapFromDTO(policyWriteDTO);
        return groupRepository.save(group);
    }

    public Optional<Group> get(UUID id) {
        return groupRepository.findById(id);
    }

    public void edit(Group group, GroupWriteDTO groupWriteDTO) {
        serverMapper.update(group, groupWriteDTO);
        groupRepository.save(group);
    }

    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public List<Group> all() {
        return groupRepository.findAll();
    }
}
