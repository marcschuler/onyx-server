package de.marcschuler.webrtcserver.dto.data.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.marcschuler.webrtcserver.data.message.MessageContentType;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MarkdownMessageContentDTO.class, name = "MARKDOWN"),
})
@Data
public sealed abstract class MessageContentDTO permits MarkdownMessageContentDTO {
    private MessageContentType type;
}
