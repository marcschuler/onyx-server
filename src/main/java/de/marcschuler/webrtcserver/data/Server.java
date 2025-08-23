package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@Data
public class Server {
    @NotNull
    private UUID id;
    @Size(min = 3, max = 64)
    private String name;

    @NotNull
    private List<Section> sections;

    @NotNull
    private Channel defaultChannel;

    @JsonIgnore
    @ToString.Exclude
    private PrivateKey privateKey;
    private PublicKey publicKey;
}
