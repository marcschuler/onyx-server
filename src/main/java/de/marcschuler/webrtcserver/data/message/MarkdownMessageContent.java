package de.marcschuler.webrtcserver.data.message;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MARKDOWN")
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class MarkdownMessageContent extends MessageContent {
    @Column(length = 65535)
    private String text;
}
