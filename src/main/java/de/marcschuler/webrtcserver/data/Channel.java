package de.marcschuler.webrtcserver.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="channels")
    private Section section;
}
