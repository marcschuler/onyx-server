package de.marcschuler.onyxserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, length = 32)
    private String name;

    @Size(max = 32)
    private String password;
    @NotNull
    private VoiceMode voiceMode = VoiceMode.FULL;
    @NotNull
    private ChatMode chatMode = ChatMode.FULL;

    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
    private Chat chat;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Section section;

    public enum VoiceMode {
        NONE,
        FULL
    }

    public enum ChatMode {
        NONE,
        TEMPORARY,
        FULL
    }
}

