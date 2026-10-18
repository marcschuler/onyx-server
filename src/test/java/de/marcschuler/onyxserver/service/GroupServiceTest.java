package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.GroupCreateDTO;
import de.marcschuler.onyxserver.dto.data.GroupDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Transactional
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private TestService testService;

    @Test
    void testCreate() {
        var dto = new GroupCreateDTO("New Group", "A test group", false, false);

        var group = groupService.create(dto);

        assertNotNull(group.getId());
        assertEquals("New Group", group.getName());
        assertEquals("A test group", group.getDescription());
    }

    @Test
    void testGet() {
        var group = testService.groupAdmin();
        var found = groupService.get(group.getId());

        assertTrue(found.isPresent());
        assertEquals(group.getId(), found.get().getId());
    }

    @Test
    void testGetNotFound() {
        var found = groupService.get(UUID.randomUUID());
        assertTrue(found.isEmpty());
    }

    @Test
    void testEdit() {
        var group = testService.groupAdmin();
        var dto = new GroupDTO();
        dto.setName("Renamed Group");
        dto.setDescription(group.getDescription());
        dto.setPriority(group.getPriority());
        dto.setDefaultForNewUsers(group.isDefaultForNewUsers());
        dto.setLabel(group.isLabel());

        groupService.edit(group, dto);

        assertEquals("Renamed Group", group.getName());
    }

    @Test
    void testDelete() {
        var group = testService.groupAdmin();
        var server = testService.sectionLobby().getServer();

        groupService.delete(server, group);

        assertFalse(server.getGroups().contains(group));
        assertTrue(groupService.get(group.getId()).isEmpty());
    }

    @Test
    void testAll() {
        var groups = groupService.all();
        assertFalse(groups.isEmpty());
    }

    @Test
    void testGetGroupsDefaultForNewUser() {
        var groups = groupService.getGroupsDefaultForNewUser();
        assertEquals(1, groups.size());
        assertTrue(groups.getFirst().isDefaultForNewUsers());
    }
}
