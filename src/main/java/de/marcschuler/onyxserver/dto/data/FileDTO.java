package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class FileDTO {
    @NotNull
    private UUID id;

    @NotNull
    private String filename;
    @NotNull
    private String contentType;

    @NotNull
    private HashDTO hash;
    @NotNull
    private Instant created;

    @NotNull
    private long size;

}
