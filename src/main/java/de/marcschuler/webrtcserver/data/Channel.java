package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @ManyToOne(cascade = CascadeType.ALL, optional = false,fetch =  FetchType.EAGER)
    private Chat chat;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Section section;

}
