package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final FileRepository fileRepository;

    @Value("${iris.storage.path}")
    private Path basePath;

    public File uploadFile(MultipartFile file) throws IOException {
        var bytes = file.getInputStream().readAllBytes();
        var hash = hash(bytes);
        var path = filePath(hash);
        Files.write(path, bytes);
        var f = new File();
        f.setFilename(file.getOriginalFilename());
        f.setContentType(file.getContentType());
        f.setHashType(File.HashType.SHA_256);
        f.setHash(hash);
        fileRepository.save(f);
        return f;
    }

    public InputStream getFileContent(File file) throws IOException {
        //TODO check if it is an sha256 and no path injection?
        var path = filePath(file.getHash());
        return Files.newInputStream(path);
    }

    public Path filePath(String hash) {
        var firstByte = hash.substring(0, 2);
        return basePath.resolve(firstByte).resolve(hash);
    }

    public String hash(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }
}
