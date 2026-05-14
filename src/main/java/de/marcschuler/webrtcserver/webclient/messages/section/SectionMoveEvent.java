package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class SectionMoveEvent extends MessageBody {
    @NotNull
    private UUID sectionId;
    @NotNull
    private int order;
}
