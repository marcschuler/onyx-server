package de.marcschuler.webrtcserver.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    private UUID id;
    private String name;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "section", orphanRemoval = true)
    @OrderColumn
    private List<Channel> channels;
}
