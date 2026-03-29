package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class PolicyItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    @Column(nullable = false)
    @OrderColumn(name = "policies_order")
    private List<Policy> policies = new ArrayList<>();
}
