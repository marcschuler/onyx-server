package de.marcschuler.webrtcserver.dto.data.message;

import lombok.Data;

@Data
public final class MarkdownMessageContentDTO extends MessageContentDTO {
    private String text;
}
