package de.marcschuler.webrtcserver.controller.v0;


import de.marcschuler.webrtcserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/storage/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @GetMapping(value = "{fileId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStream> file(@PathVariable UUID fileId) throws IOException {
        var file = storageService.get(fileId).orElseThrow();
        return storageService.buildResponse(file);
    }
}
