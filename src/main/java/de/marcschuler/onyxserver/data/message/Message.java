package de.marcschuler.onyxserver.data.message;

import de.marcschuler.onyxserver.data.Chat;
import de.marcschuler.onyxserver.data.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

//TODO Users should be able to sign their message (content + timestamp)
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

    @ManyToOne
    private Message repliesTo;
}
