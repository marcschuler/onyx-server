package de.marcschuler.webrtcserver.webclient.messages.channel;

import de.marcschuler.webrtcserver.dto.ChannelReference;
import de.marcschuler.webrtcserver.webclient.messages.MessageBodyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChannelDetailResponse extends MessageBodyResponse<ChannelDetailRequest> {

    private ChannelReference channel;


}
