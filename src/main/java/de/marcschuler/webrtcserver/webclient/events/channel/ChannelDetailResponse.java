package de.marcschuler.webrtcserver.webclient.events.channel;

import de.marcschuler.webrtcserver.dto.ChannelReference;
import de.marcschuler.webrtcserver.webclient.events.EventBodyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChannelDetailResponse extends EventBodyResponse<ChannelDetailRequest> {

    private ChannelReference channel;


}
