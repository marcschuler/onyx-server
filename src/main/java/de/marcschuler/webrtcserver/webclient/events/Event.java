package de.marcschuler.webrtcserver.webclient.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginEvent.class, name = "CLIENT_LOGIN_EVENT"),
        @JsonSubTypes.Type(value = ChannelChangeRequest.class, name = "CLIENT_CHANNEL_CHANGE_REQUEST"),
        @JsonSubTypes.Type(value = ClientPeerOffer.class, name = "CLIENT_PEER_OFFER"),
        @JsonSubTypes.Type(value = ServerPeerOfferForward.class, name = "SERVER_PEER_OFFER_FORWARD")
})
@Getter
@Setter
public abstract class Event {

    protected EventType type;

    public enum EventType {
        SERVER_INFO_TREE,
        CLIENT_LOGIN_EVENT,
        CLIENT_CHANNEL_CHANGE_REQUEST,
        CLIENT_PEER_OFFER,
        SERVER_PEER_OFFER_FORWARD,
    }
}
