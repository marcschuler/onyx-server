package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.data.file.Hash;
import de.marcschuler.onyxserver.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
class StorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    private StorageService storageService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        storageService = new StorageService(fileRepository);

        // Create a temporary directory that will be used as the basePath
        var basePath = Files.createTempDirectory("storage-test");
        // Inject the Path into the service via reflection
        setField(storageService, "basePath", basePath);
    }

    // Utility to set a private field via reflection
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ------------------------------------------------------------------ */
    @Test
    void uploadFile_savesFileAndReturnsMetadata() throws IOException {
        // GIVEN
        var originalFilename = "picture.png";
        var contentType = "image/png";
        var bytes = "Hello, World!".getBytes();
        var multipartFile = new MockMultipartFile(
                "file", originalFilename, contentType,
                new ByteArrayInputStream(bytes));

        // Mock repository to return empty Optional for any UUID
        when(fileRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // WHEN
        File stored = storageService.uploadFile(multipartFile);

        // THEN
        // 1⃣ Verify file was persisted
        verify(fileRepository, times(1)).save(stored);
        assertEquals(bytes.length, stored.getSize());
        assertEquals(originalFilename, stored.getFilename());
        assertEquals(contentType, stored.getContentType());
        assertNotNull(stored.getHash());
        assertEquals(Hash.HashType.SHA_256, stored.getHash().getType());

        // 2⃣ Verify hash calculation is consistent with SHA‑256
        var expectedHash = DigestUtils.sha256Hex(bytes);
        assertEquals(expectedHash, stored.getHash().getHash());

        // 3⃣ Verify that the file path uses the first two bytes
        var expectedPath = storageService.filePath(stored.getHash());
        assertTrue(Files.exists(expectedPath));
        assertArrayEquals(bytes, Files.readAllBytes(expectedPath));
    }

    /* ------------------------------------------------------------------ */
    @Test
    void buildResponse_returnsCorrectEntity() throws IOException {
        // GIVEN
        var file = new File();
        file.setId(UUID.randomUUID());
        file.setFilename("example.txt");
        file.setContentType("text/plain");
        file.setSize(11L);
        file.setHash(new Hash(Hash.HashType.SHA_256, "dummyhash1234567890123456"));
        file.setCreated(Instant.now());

        // Pretend the file exists on disk
        var fakePath = storageService.filePath(file.getHash());
        Files.createDirectories(fakePath.getParent());
        Files.write(fakePath, "Hello world".getBytes());

        // WHEN
        var response = storageService.buildResponse(file);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"example.txt\"", response.getHeaders()
                .getFirst(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION));
        assertArrayEquals("Hello world".getBytes(), response.getBody());
    }

    /* ------------------------------------------------------------------ */
    @Test
    void isImageType_identifiesJpegAndPngOnly() {
        // GIVEN
        var jpeg = new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3});
        var png = new MockMultipartFile("image", "graphic.png", MediaType.IMAGE_PNG_VALUE,
                new byte[]{4, 5, 6});
        var txt = new MockMultipartFile("file", "note.txt", MediaType.TEXT_PLAIN_VALUE,
                new byte[]{7, 8});

        // WHEN/THEN
        assertTrue(storageService.isImageType(jpeg));
        assertTrue(storageService.isImageType(png));
        assertFalse(storageService.isImageType(txt));
    }

    /* ------------------------------------------------------------------ */
}