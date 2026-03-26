package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.dto.data.UserExtendedDTO;
import de.marcschuler.webrtcserver.mapper.GroupMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.GroupService;
import de.marcschuler.webrtcserver.service.StorageService;
import de.marcschuler.webrtcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/user/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final StorageService storageService;

    private final UserService userService;
    private final GroupService groupService;

    private final ServerMapper serverMapper;
    private final GroupMapper groupMapper;

    @GetMapping
    public List<UserExtendedDTO> users() {
        return userService.all()
                .stream().map(serverMapper::mapToDTOExtended)
                .toList();
    }


    @PostMapping("{id}/profile/avatar")
    public void uploadMedia(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        var f = storageService.uploadFile(file);
        var user = userService.findById(id).orElseThrow();
        userService.setUserAvatar(user, f);
        serverMapper.mapToDTO(f);
    }

    @PutMapping("{id}/state/ban")
    public UserExtendedDTO ban(@PathVariable String id){
        var user = userService.findById(id).orElseThrow();
        userService.ban(user);
        return serverMapper.mapToDTOExtended(user);
    }

    @PutMapping("{id}/state/unban")
    public UserExtendedDTO unban(@PathVariable String id){
        var user = userService.findById(id).orElseThrow();
        user.setState(ClientState.ACTIVE);
        userService.save(user);
        return serverMapper.mapToDTOExtended(user);
    }

    @PutMapping("{id}/state/active")
    public UserExtendedDTO active(@PathVariable String id){
        var user = userService.findById(id).orElseThrow();
        user.setState(ClientState.ACTIVE);
        userService.save(user);
        return serverMapper.mapToDTOExtended(user);
    }


    @PutMapping("{id}/groups/{groupId}")
    public List<GroupDTO> groupsPut(@PathVariable String id, @PathVariable UUID groupId) {
        var user = userService.findById(id).orElseThrow();
        var group = groupService.get(groupId).orElseThrow();
        user.getGroups().add(group);
        userService.save(user);
        return groupMapper.mapToDTO(user.getGroups());
    }

    @DeleteMapping("{id}/groups/{groupId}")
    public List<GroupDTO> groupsDelete(@PathVariable String id, @PathVariable UUID groupId) {
        var user = userService.findById(id).orElseThrow();
        var group = groupService.get(groupId).orElseThrow();
        if (!user.getGroups().remove(group)) {
            throw new IllegalStateException("User is not in group");
        }
        userService.save(user);
        return groupMapper.mapToDTO(user.getGroups());
    }
}
