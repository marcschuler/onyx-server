package de.marcschuler.onyxserver.dto.data.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class MarkdownMessageContentDTO extends MessageContentDTO {
    @NotNull
    private String text;
}
