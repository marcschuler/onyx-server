package de.marcschuler.onyxserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jose.jwk.OctetKeyPair;
import de.marcschuler.onyxserver.data.file.File;
import de.marcschuler.onyxserver.data.message.MessageContent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

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
    private File icon;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "server_id")
    @OrderColumn(name = "section_order")
    private List<Section> sections = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, optional = false, mappedBy = "server")
    private ServerSettings settings;


    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderColumn(name = "description_order")
    private List<MessageContent> description;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Invite> invites;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Group> groups = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Column(length = 4096)
    @Lob
    private OctetKeyPair keys;
}
