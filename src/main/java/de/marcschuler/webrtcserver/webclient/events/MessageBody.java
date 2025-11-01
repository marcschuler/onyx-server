package de.marcschuler.webrtcserver.webclient.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@Getter
@Setter
public abstract class MessageBody {

}
