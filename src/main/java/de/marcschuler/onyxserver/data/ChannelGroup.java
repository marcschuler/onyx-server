package de.marcschuler.onyxserver.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Data
public class ChannelGroup {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Channel channel;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();
}
