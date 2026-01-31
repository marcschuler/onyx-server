package de.marcschuler.webrtcserver.dto.data;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FileDTO {
    private UUID id;

    private String filename;
    private String contentType;

    private HashDTO hash;
    private Instant created;

}
