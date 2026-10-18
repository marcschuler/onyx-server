package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.data.file.Hash;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    private Path basePath;
    private Path cacheBasePath;
    private StorageService storageService;

    @BeforeEach
    void setUp() throws IOException {
        basePath = Files.createTempDirectory("storage-test");
        cacheBasePath = Files.createTempDirectory("cache-test");
        storageService = new StorageService(basePath, cacheBasePath);
    }

    @Test
    void writeFile_writesContentAndReturnsSamePath() throws IOException {
        var bytes = "Hello, World!".getBytes();
        var hash = new Hash(Hash.HashType.SHA_256, DigestUtils.sha256Hex(bytes));

        storageService.writeFile(hash, bytes);

        var expectedPath = storageService.pathFromHash(hash);
        assertTrue(Files.exists(expectedPath));
        assertArrayEquals(bytes, Files.readAllBytes(expectedPath));
    }

    @Test
    void filePath_usesFirstTwoBytesAsFolder() {
        var hashValue = "abcd1234567890abcdef1234567890abcdef1234567890abcdef1234567890ab";
        var hash = new Hash(Hash.HashType.SHA_256, hashValue);

        var path = storageService.filePath(hash);

        assertEquals(basePath.resolve("sha256-ab").resolve(hashValue), path);
    }

    @Test
    void buildResponse_returnsCorrectEntity() throws IOException {
        var file = new File();
        file.setId(java.util.UUID.randomUUID());
        file.setFilename("example.txt");
        file.setContentType("text/plain");
        file.setSize(11L);
        file.setHash(new Hash(Hash.HashType.SHA_256, "dummyhash1234567890123456abcdef12345678901234567890abcdef123456"));
        file.setCreated(Instant.now());

        var fakePath = storageService.pathFromHash(file.getHash());
        Files.createDirectories(fakePath.getParent());
        Files.write(fakePath, "Hello world".getBytes());

        var response = storageService.buildResponse(file);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"example.txt\"", response.getHeaders()
                .getFirst(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION));
        assertArrayEquals("Hello world".getBytes(), response.getBody());
    }

    @Test
    void isImageType_identifiesJpegAndPngOnly() {
        var jpeg = new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3});
        var png = new MockMultipartFile("image", "graphic.png", MediaType.IMAGE_PNG_VALUE,
                new byte[]{4, 5, 6});
        var txt = new MockMultipartFile("file", "note.txt", MediaType.TEXT_PLAIN_VALUE,
                new byte[]{7, 8});

        assertTrue(storageService.isImageType(jpeg));
        assertTrue(storageService.isImageType(png));
        assertFalse(storageService.isImageType(txt));
    }

    @Test
    void hash_returnsSha256Hex() {
        var data = "hash-me".getBytes();

        var result = storageService.hash(data);

        assertEquals(DigestUtils.sha256Hex(data), result);
    }
}
