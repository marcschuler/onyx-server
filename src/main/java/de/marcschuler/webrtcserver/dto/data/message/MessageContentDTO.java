package de.marcschuler.webrtcserver.dto.data.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.marcschuler.webrtcserver.data.message.MessageContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MarkdownMessageContentDTO.class, name = "MARKDOWN"),
        @JsonSubTypes.Type(value = FileMessageContentDTO.class, name = "FILE"),
})
@Data
public abstract class MessageContentDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
}
