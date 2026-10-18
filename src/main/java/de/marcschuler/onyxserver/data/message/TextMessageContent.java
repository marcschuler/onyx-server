package de.marcschuler.onyxserver.data.message;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TEXT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageContent extends MessageContent{
    @Column(length = 65535)
    private String text;
}
