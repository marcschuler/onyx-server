package de.marcschuler.webrtcserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelCreateDTO extends ChannelWriteDTO{
    @NotNull
    private UUID sectionId;
    @NotNull
    private int order;
}
