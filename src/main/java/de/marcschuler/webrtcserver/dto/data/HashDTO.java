package de.marcschuler.webrtcserver.dto.data;

import de.marcschuler.webrtcserver.data.Hash;
import lombok.Data;

@Data
public class HashDTO {
    private Hash.HashType type;
    private String hash;
}
