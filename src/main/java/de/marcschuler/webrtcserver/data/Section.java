package de.marcschuler.webrtcserver.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    private UUID id;
    private String name;

    @OneToMany(
            mappedBy = "section",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    //TODO @OrderColumn(name = "channel_order")
    private List<Channel> channels = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="server_id")
    @NotNull
    private Server server;
}
