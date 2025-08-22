package de.marcschuler.webrtcserver.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class Section {
    @Size(min = 3, max = 32)
    private String name;
    @NotNull
    private List<Channel> channel;
}
