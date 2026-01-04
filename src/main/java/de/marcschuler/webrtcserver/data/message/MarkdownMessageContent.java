package de.marcschuler.webrtcserver.data.message;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("MARKDOWN")
@Data
public final class MarkdownMessageContent extends MessageContent {
    @Column(length = 65535)
    private String text;
}
