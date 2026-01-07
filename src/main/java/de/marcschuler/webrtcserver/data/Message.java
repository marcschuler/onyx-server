package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.SignedContent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

//TODO User should be able to sign their message (content + timestamp)
@Entity
@Data
public class Message {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private Instant timestamp;

    @OneToMany(cascade =  CascadeType.ALL,orphanRemoval = true)
    private List<MessageContent> content;

    @ManyToOne
    @JoinColumn(name="chat_id")
    @NotNull
    private Chat chat;
}
