package de.marcschuler.onyxserver.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ChannelExtendedDTO extends ChannelDTO {
    @NotNull
    private List<UserSimpleDTO> users;
}
