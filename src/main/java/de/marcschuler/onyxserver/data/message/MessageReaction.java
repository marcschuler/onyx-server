package de.marcschuler.onyxserver.data.message;

import de.marcschuler.onyxserver.data.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * An emoji reaction of a user to a message. A user can react with each emoji only once per message.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "UK_MESSAGE_REACTION_MESSAGE_USER_EMOJI",
        columnNames = {"message_id", "user_id", "emoji"}))
@Data
public class MessageReaction {

    public static final int MAX_EMOJI_LENGTH = 64;

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column(nullable = false)
    @NotNull
    private Instant timestamp;

    @Column(nullable = false, length = MAX_EMOJI_LENGTH)
    @NotBlank
    @Size(max = MAX_EMOJI_LENGTH)
    private String emoji;

    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Message message;
}
