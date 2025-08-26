package de.marcschuler.webrtcserver.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @NotNull
    private UUID id;
    @Size(min = 3, max = 64)
    private String name;
}
