package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jose.jwk.OctetKeyPair;
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
    private MarkdownMessageContent description; //TODO List<MessageContent>?

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "server_id")
    @OrderColumn(name = "section_order")
    private List<Section> sections = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Group> groups = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Column(length = 4096)
    @Lob
    private OctetKeyPair keys;
}
