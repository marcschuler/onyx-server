package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.dto.GroupCreateDTO;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.mapper.GroupMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;

    private final GroupMapper groupMapper;

    public Group create(GroupCreateDTO groupCreateDTO) {
        var group = groupMapper.mapFromDTO(groupCreateDTO);
        return groupRepository.save(group);
    }


    public Optional<Group> get(UUID id) {
        return groupRepository.findById(id);
    }

    public void edit(Group group, GroupDTO groupWriteDTO) {
        groupMapper.update(group, groupWriteDTO);
        groupRepository.save(group);
    }

    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public List<Group> all() {
        return groupRepository.findAll();
    }

    public List<Group> getGroupsDefaultForNewUser() {
        return groupRepository.findGroupByDefaultForNewUsersIsTrue();
    }
}
