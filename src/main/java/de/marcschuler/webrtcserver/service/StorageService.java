package de.marcschuler.webrtcserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    @Value("${iris.storage.path}")
    private Path basePath;

    public InputStream getFile(String id, FileType type) throws IOException {
        //TODO check if it is an sha256 and no path injection
        var firstByte = id.substring(0,2);
        var lastBytes = id.substring(3);
        var path = type.baseFolder(basePath).resolve(firstByte).resolve(lastBytes);
        return Files.newInputStream(path);
    }


    public enum FileType implements StorageFileType{
        SERVER,
        USER_PROFILE_IMAGE;

        @Override
        public Path baseFolder(Path base) {
            return base.resolve(name().toLowerCase());
        }
    }

    public interface StorageFileType{
        Path baseFolder(Path base);
    }
}
