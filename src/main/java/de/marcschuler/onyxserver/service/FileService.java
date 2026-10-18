package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.data.file.Hash;
import de.marcschuler.onyxserver.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;

    public Optional<File> get(UUID id) {
        return fileRepository.findById(id);
    }

    public File uploadFile(MultipartFile file) throws IOException {
        var bytes = file.getInputStream().readAllBytes(); //TODO use input stream for larger files
        var hash = new Hash(Hash.HashType.SHA_256, storageService.hash(bytes));
        storageService.writeFile(hash, bytes);
        var f = new File();
        f.setSize(bytes.length);
        f.setCreated(Instant.now());
        f.setFilename(file.getOriginalFilename());
        f.setContentType(file.getContentType());
        f.setHash(hash);
        fileRepository.save(f);
        return f;
    }


}
