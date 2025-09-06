package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Server {
    @Id
    @NotNull
    private UUID id;
    @Size(min = 3, max = 64)
    private String name;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<Section> sections;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @Deprecated
    private Channel defaultChannel;

    @JsonIgnore
    @ToString.Exclude
    @Column(length=1024)
    private KeyPair keys;
}
