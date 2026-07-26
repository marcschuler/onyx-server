package de.marcschuler.onyxserver.dto.data;

import lombok.Data;

import java.util.List;

@Data
public class OnlineChannelDTO extends ChannelDTO {
    private List<UserSimpleDTO> clients;
}
