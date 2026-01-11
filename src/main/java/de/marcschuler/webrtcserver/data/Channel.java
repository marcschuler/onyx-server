package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.dto.data.ChannelCreateDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Chat chat;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;

}
