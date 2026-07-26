package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.dto.data.FileDTO;
import de.marcschuler.onyxserver.dto.data.ServerDTO;
import de.marcschuler.onyxserver.error.ServerNotFoundException;
import de.marcschuler.onyxserver.mapper.MessageContentMapper;
import de.marcschuler.onyxserver.mapper.ServerMapper;
import de.marcschuler.onyxserver.service.PermissionService;
import de.marcschuler.onyxserver.service.ServerService;
import de.marcschuler.onyxserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/server/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final PermissionService permissionService;

    private final StorageService storageService;

    private final ServerMapper serverMapper;
    private final MessageContentMapper messageContentMapper;

    @PutMapping("{serverId}")
    public ServerDTO edit(@PathVariable UUID serverId, @RequestBody ServerDTO serverDto) {
        permissionService.checkControllerAccess(null, null, PermissionType.SERVER_EDIT);

        var server = serverService.get(serverId).orElseThrow(()->new ServerNotFoundException(serverId));
        server = serverService.update(server, serverDto);
        return serverMapper.mapToDTO(server);
    }

    @PostMapping(value = "{serverId}/icon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO iconUpload(@PathVariable UUID serverId, @RequestParam("file") MultipartFile file) throws IOException {
        permissionService.checkControllerAccess(null, null, PermissionType.SERVER_EDIT);
        var server = serverService.get(serverId).orElseThrow();

        if (!storageService.isImageType(file))
            throw new FileUploadException("Only image files are allowed (jpg,png)");
        var f = storageService.uploadFile(file);
        serverService.setIcon(server,f);
        return serverMapper.mapToDTO(f);
    }

    @DeleteMapping("{serverId}/icon")
    public void iconDelete(@PathVariable UUID serverId) {
        permissionService.checkControllerAccess(null, null, PermissionType.SERVER_EDIT);

        var server = serverService.get(serverId).orElseThrow();
        serverService.setIcon(server,null);
    }

    @GetMapping("{serverId}")
    public ServerDTO get(@PathVariable UUID serverId) {
        return serverMapper.mapToDTO(serverService.get(serverId).orElseThrow(() -> new ServerNotFoundException(serverId)));
    }

}
