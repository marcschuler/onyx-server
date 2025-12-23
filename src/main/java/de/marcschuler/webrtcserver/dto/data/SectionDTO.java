package de.marcschuler.webrtcserver.dto.data;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SectionDTO extends SectionWriteDTO {
    private UUID id;
    private List<ChannelDTO> channels;

}
