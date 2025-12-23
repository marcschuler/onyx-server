package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.webclient.messages.MessageBodyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChannelDetailResponse extends MessageBodyResponse<ChannelDetailRequest> {

    private ChannelDTO channel;


}
