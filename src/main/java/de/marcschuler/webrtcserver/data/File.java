package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class File {
    @Id
    @GeneratedValue
    private UUID id;

    private String filename;
    private String contentType;

    private HashType hashType;
    private String hash;
    private String previewHash;

    @CreatedDate
    private Instant created;

    public enum HashType{
        SHA_256
    }
}
