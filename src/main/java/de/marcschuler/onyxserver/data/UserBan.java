package de.marcschuler.onyxserver.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * A ban of a {@link User}. A user is banned as long as {@link User#getBan()} is set.
 */
@Entity(name = "USER_BAN")
@Data
public class UserBan {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Reason shown to the banned user, may be null
     */
    @Nullable
    @Column(length = 1024)
    private String reason;

    @NotNull
    @Column(nullable = false)
    private Instant bannedAt;

    /**
     * The user who issued the ban, null if banned by the system
     */
    @Nullable
    @ManyToOne
    @ToString.Exclude
    private User bannedBy;
}
