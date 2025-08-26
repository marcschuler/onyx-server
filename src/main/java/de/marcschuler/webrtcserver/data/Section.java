package de.marcschuler.webrtcserver.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Size(min = 3, max = 32)
    private String name;
    @NotNull
    private List<Channel> channel;
}
