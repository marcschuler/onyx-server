package de.marcschuler.webrtcserver.controller.v0;


import de.marcschuler.webrtcserver.data.file.PreviewFormat;
import de.marcschuler.webrtcserver.dto.data.FileDTO;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/storage/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    private final ServerMapper serverMapper;

    @GetMapping(value = "{fileId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> file(@PathVariable UUID fileId) throws IOException {
        var file = storageService.get(fileId).orElseThrow();
        return storageService.buildResponse(file);
    }

    @GetMapping("{fileId}/preview/{format}")
    public ResponseEntity<byte[]> filePreview(@PathVariable UUID fileId, @PathVariable PreviewFormat format) throws IOException {
        var file = storageService.get(fileId).orElseThrow();
        return storageService.buildPreviewResponse(file, format);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        var f = storageService.uploadFile(file);
        return serverMapper.mapToDTO(f);
    }
}
