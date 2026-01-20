package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.UserExtendedDTO;
import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
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
    private final ServerMapper serverMapper;

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
}
