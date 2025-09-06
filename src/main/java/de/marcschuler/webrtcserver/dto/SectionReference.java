package de.marcschuler.webrtcserver.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionReference {
    private UUID id;
    private String name;
    private List<ChannelReference> channels;
}
