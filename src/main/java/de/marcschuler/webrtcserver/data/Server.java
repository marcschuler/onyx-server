package de.marcschuler.webrtcserver.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
}
