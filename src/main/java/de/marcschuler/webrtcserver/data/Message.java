package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.dto.SignedContent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
public class Message {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(length = 65535)
    private String markdown;

    @ManyToOne
    @JoinColumn(name="chat_id")
    @NotNull
    private Chat chat;
}
