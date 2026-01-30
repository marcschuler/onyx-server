package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Server {
    @Id
    @NotNull
    @GeneratedValue
    private UUID id;
    @Size(min = 3, max = 64)
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private MarkdownMessageContent description;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "server_id")
    @OrderColumn(name = "channel_order")
    private List<Section> sections = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Group> groups = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Column(length = 1024)
    private KeyPair keys;
}
