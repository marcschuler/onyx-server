package de.marcschuler.webrtcserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChannelReference {
    private UUID id;
    private String name;
}
