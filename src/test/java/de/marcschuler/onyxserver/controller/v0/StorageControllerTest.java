package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.OnyxTest;
import de.marcschuler.onyxserver.TestService;
import de.marcschuler.onyxserver.dto.data.FileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OnyxTest
class StorageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestService testService;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", testService.bearerToken(testService.userAdmin()))
                .build();
    }

    @Test
    void testUploadAndDownload() {
        var fileContent = "test file content";
        var resource = new ByteArrayResource(fileContent.getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        };

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", resource);

        var uploaded = restClient.post()
                .uri("/v0/storage/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(FileDTO.class);
        assertNotNull(uploaded);
        assertNotNull(uploaded.getId());
        assertEquals("test.txt", uploaded.getFilename());

        var downloaded = restClient.get()
                .uri("/v0/storage/{fileId}/download", uploaded.getId())
                .retrieve()
                .body(byte[].class);
        assertArrayEquals(fileContent.getBytes(), downloaded);
    }

    @Test
    void testDownloadNotFound() {
        assertThrows(Exception.class, () ->
                restClient.get()
                        .uri("/v0/storage/{fileId}/download", UUID.randomUUID())
                        .retrieve()
                        .toBodilessEntity());
    }

    @Test
    void testPreviewNotFound() {
        assertThrows(Exception.class, () ->
                restClient.get()
                        .uri("/v0/storage/{fileId}/preview/{format}", UUID.randomUUID(), "THUMBNAIL")
                        .retrieve()
                        .toBodilessEntity());
    }
}
