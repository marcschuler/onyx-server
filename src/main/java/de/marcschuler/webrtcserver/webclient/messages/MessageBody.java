package de.marcschuler.webrtcserver.webclient.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@Getter
@Setter
public abstract class MessageBody {

}
