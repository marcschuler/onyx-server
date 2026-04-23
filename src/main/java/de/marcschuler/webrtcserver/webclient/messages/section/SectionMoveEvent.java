package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class SectionMoveEvent extends MessageBody {
    private UUID sectionId;
    private int order;
}
