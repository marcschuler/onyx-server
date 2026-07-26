package de.marcschuler.onyxserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(length = 32)
    private String name;

    @OneToMany(
            cascade = CascadeType.ALL,
             fetch = FetchType.EAGER
    )
    @JoinColumn(name="section_id")
    @OrderColumn(name = "channel_order")
    private List<Channel> channels = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "server_id", insertable = false, updatable = false)
    private Server server;
}
