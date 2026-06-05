package de.marcschuler.webrtcserver.data.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

/**
 * An entity that represents a file.
 * Once created, a file can never be edited again.
 * Note that all files with the same hash (eq: same data) can be represented by
 * one physical file in the file system.
 * Not using the hash as ID because with permissions we may not want
 * to show if a file representing a hash already exists with e.q. a different filename
 */
@Entity
@Data
public class File {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,updatable = false) @Size(min = 3)
    private String filename;
    @Column(nullable = false,updatable = false)
    private String contentType;
    @Column(updatable = false)
    @Min(1)
    private long size;

    @Column(nullable = false,updatable = false)
    private Hash hash;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;


}
