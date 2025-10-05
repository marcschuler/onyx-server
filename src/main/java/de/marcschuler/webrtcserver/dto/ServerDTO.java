package de.marcschuler.webrtcserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ServerDTO extends ServerWritableDTO {
    private UUID id;
    private String name;
    private byte[] publicKey;
}
