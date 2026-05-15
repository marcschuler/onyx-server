package de.marcschuler.webrtcserver.webclient.messages.section;

import de.marcschuler.webrtcserver.dto.data.SectionDTO;
import de.marcschuler.webrtcserver.dto.data.SectionExtendedDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBody;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionCreateEvent extends MessageBody {
    @NotNull
    private SectionExtendedDTO section;
    @NotNull
    private int order;
}
