package de.marcschuler.webrtcserver.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Client {
    @NotNull
    private String id;
    @NotNull
    @Size(min = 3, max = 32)
    private String username;
}
