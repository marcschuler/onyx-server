package de.marcschuler.webrtcserver.dto.data;

import lombok.Data;

import java.util.UUID;

@Data
public class ChannelDTO extends ChannelWriteDTO {
    private UUID id;
}
