package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.data.Hash;
import de.marcschuler.webrtcserver.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * Files are saved in the directory specified in the environment.
 * Currently we create sha256 hashes and use that as path.
 * To seperate files into directories we use the first two bytes
 * as folder name, e.q. f1/f1c40...f
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final FileRepository fileRepository;

    @Value("${onyx.storage.files}")
    private Path basePath;

    public Optional<File> get(UUID id) {
        return fileRepository.findById(id);
    }

    public File uploadFile(MultipartFile file) throws IOException {
        var bytes = file.getInputStream().readAllBytes(); //TODO use input stream for larger files
        var hash = new Hash(Hash.HashType.SHA_256, hash(bytes));
        var path = filePath(hash);
        Files.write(path, bytes);
        var f = new File();
        f.setSize(bytes.length);
        f.setFilename(file.getOriginalFilename());
        f.setContentType(file.getContentType());
        f.setHash(hash);
        fileRepository.save(f);
        return f;
    }

    public ResponseEntity<InputStream> buildResponse(File file) throws IOException {
        var disposition = ContentDisposition.attachment()
                .filename(file.getFilename())
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentLength(file.getSize())
                .body(streamContent(file));
    }

    public InputStream streamContent(File file) throws IOException {
        var path = filePath(file.getHash());
        return Files.newInputStream(path);
    }

    public Path filePath(Hash hash) {
        var hashString = StringUtils.cleanPath(hash.getHash());
        var typeString = hash.getType().getSafeName();
        var firstBytes = hashString.substring(0, 2);
        return basePath.resolve(typeString + "-" + firstBytes).resolve(hashString);
    }

    public String hash(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }
}
