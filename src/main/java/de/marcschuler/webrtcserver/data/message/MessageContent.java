package de.marcschuler.webrtcserver.data.message;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "type")
@Data
public sealed abstract class MessageContent permits MarkdownMessageContent  {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    private MessageContentType type;

}
