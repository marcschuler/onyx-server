package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.dto.data.FileDTO;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.dto.data.UserExtendedDTO;
import de.marcschuler.webrtcserver.mapper.GroupMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.GroupService;
import de.marcschuler.webrtcserver.service.StorageService;
import de.marcschuler.webrtcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/user/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GroupService groupService;

    private final StorageService storageService;

    private final ServerMapper serverMapper;
    private final GroupMapper groupMapper;

    @GetMapping
    public List<UserExtendedDTO> users() {
        return userService.all()
                .stream().map(serverMapper::mapToDTOExtended)
                .toList();
    }

    @PutMapping("{id}/state/ban")
    public UserExtendedDTO ban(@PathVariable String id, @RequestBody String message) {
        var user = userService.findById(id).orElseThrow();
        userService.ban(user, message);
        return serverMapper.mapToDTOExtended(user);
    }

    @PutMapping("{id}/state/unban")
    public UserExtendedDTO unban(@PathVariable String id) {
        var user = userService.findById(id).orElseThrow();
        user.setState(ClientState.ACTIVE);
        userService.save(user);
        return serverMapper.mapToDTOExtended(user);
    }

    @PutMapping("{id}/state/active")
    public UserExtendedDTO active(@PathVariable String id) {
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

    @GetMapping(value = "{id}/avatar", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> avatar(@PathVariable String id) throws IOException {
        var user = userService.findById(id).orElseThrow();
        var file = user.getAvatar();
        if (file == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return storageService.buildResponse(file);
    }


    @PostMapping(value = "{id}/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadMedia(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        if (!storageService.isImageType(file))
            throw new FileUploadException("Only image files are allowed (jpg,png)");
        var user = userService.findById(id).orElseThrow();
        var f = storageService.uploadFile(file);
        userService.setUserAvatar(user, f);
        return serverMapper.mapToDTO(f);
    }


    @DeleteMapping("{id}/avatar")
    public void avatarDelete(@PathVariable String id) {
        var user = userService.findById(id).orElseThrow();
        userService.setUserAvatar(user, null);
    }
}
