package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.data.file.Hash;
import de.marcschuler.onyxserver.data.file.PreviewFormat;
import de.marcschuler.onyxserver.error.FilePreviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Files are saved in the directory specified in the environment.
 * Currently we create sha256 hashes and use that as path.
 * To seperate files into directories we use the first two bytes
 * as folder name, e.q. f1/f1c40...f
 */
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private static final Set<MediaType> IMAGE_MEDIA_TYPES = Set.of(
            MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG
    );

    private final Path basePath;
    private final Path cacheBasePath;

    public boolean isImageType(MediaType mediaType) {
        return IMAGE_MEDIA_TYPES.contains(mediaType);
    }


    public boolean isImageType(MultipartFile file) {
        return isImageType(MediaType.parseMediaType(file.getContentType()));
    }

    public void writeFile(Hash hash, byte[] content) throws IOException {
        var path = pathFromHash(hash);
        Files.createDirectories(path.getParent());
        Files.write(path, content);
    }

    public ResponseEntity<byte[]> buildResponse(File file) throws IOException {
        var disposition = ContentDisposition.attachment()
                .filename(file.getFilename())
                .build();
        return ResponseEntity.ok()
                //.contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentLength(file.getSize())
                .body(Files.readAllBytes(filePath(file.getHash())));
    }

    public ResponseEntity<byte[]> buildPreviewResponse(File file, PreviewFormat format) {
        byte[] content = getPreviewContent(file, format);

        var disposition = ContentDisposition.attachment()
                .filename(file.getFilename() + "_preview_" + format.name() + "." + format.getFormat())
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentLength(content.length)
                .body(content);
    }

    public byte[] getPreviewContent(File file, PreviewFormat format) {
        if (!isImageType(MediaType.parseMediaType(file.getContentType()))) {
            throw new FilePreviewException("File is not an image, could not generate preview");
        }
        var previewPath = previewFilePath(file.getHash(), format);
        if (!Files.exists(previewPath)) {
            synchronized (this) {
                if (!Files.exists(previewPath)) {
                    try {
                        log.info("Creating preview of {} at {}@{}:{}", file.getFilename(), format.getFormat(), format.getResolution(), format.getQuality());
                        Files.createDirectories(previewPath.getParent());
                        var fos = Files.newOutputStream(previewPath);
                        Thumbnails.of(streamContent(file))
                                .outputFormat(format.getFormat())
                                .outputQuality(format.getQuality())
                                .size(format.getResolution(), format.getResolution())
                                .toOutputStream(fos);
                    } catch (IOException e) {
                        throw new FilePreviewException("Could not generate image preview", e);
                    }
                }
            }
        }
        try {
            return Files.readAllBytes(previewPath);
        } catch (IOException e) {
            throw new FilePreviewException("Could not read image preview", e);
        }
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

    public Path previewFilePath(Hash hash, PreviewFormat format) {
        var hashString = StringUtils.cleanPath(hash.getHash());
        var typeString = hash.getType().getSafeName();
        var firstBytes = hashString.substring(0, 2);
        return cacheBasePath.resolve(typeString + "-" + firstBytes).resolve(hashString).resolve(format.name() + "." + format.getFormat());
    }

    public String hash(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }

    public Path pathFromHash(Hash hash) {
        var hashString = StringUtils.cleanPath(hash.getHash());
        var typeString = hash.getType().getSafeName();
        var firstBytes = hashString.substring(0, 2);
        return basePath.resolve(typeString + "-" + firstBytes).resolve(hashString);
    }

}
