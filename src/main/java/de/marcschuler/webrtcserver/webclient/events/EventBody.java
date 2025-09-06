package de.marcschuler.webrtcserver.webclient.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
/*@JsonSubTypes({
        @JsonSubTypes.Type(value = ChannelChangeRequest.class, name = "CLIENT_CHANNEL_CHANGE_REQUEST"),
        @JsonSubTypes.Type(value = ClientPeerOffer.class, name = "CLIENT_PEER_OFFER"),
        @JsonSubTypes.Type(value = ServerPeerOfferForward.class, name = "SERVER_PEER_OFFER_FORWARD"),
        @JsonSubTypes.Type(value = AuthChallengeRequest.class, name = "AUTH_CHALLENGE_REQUEST"),
        @JsonSubTypes.Type(value = AuthChallengeResponse.class, name = "AUTH_CHALLENGE_RESPONSE"),
        @JsonSubTypes.Type(value = AuthSuccessEvent.class, name = "AUTH_SUCCESS_EVENT")
})*/
@Getter
@Setter
public abstract class EventBody {


    /*@RequiredArgsConstructor
    @Getter
    public enum EventType {

        AUTH_CHALLENGE_REQUEST(SERVER_TO_CLIENT),
        AUTH_CHALLENGE_RESPONSE(CLIENT_TO_SERVER),
        AUTH_SUCCESS_EVENT(SERVER_TO_CLIENT),

        SERVER_TREE_CHANGE_EVENT(SERVER_TO_CLIENT),

        CHANNEL_CHANGE_REQUEST(CLIENT_TO_SERVER),


        SERVER_INFO_TREE(SERVER_TO_CLIENT),
        CLIENT_CHANNEL_CHANGE_REQUEST(CLIENT_TO_SERVER),
        CLIENT_PEER_OFFER(CLIENT_TO_SERVER),
        SERVER_PEER_OFFER_FORWARD(SERVER_TO_CLIENT);

        private final EventTypeDirection direction;
    }

    public enum EventTypeDirection {
        SERVER_TO_CLIENT,
        CLIENT_TO_SERVER,
        OMNIDIRECTIONAL
    }*/
}
