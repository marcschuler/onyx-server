package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.marcschuler.webrtcserver.data.policy.PolicyItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Chat chat;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;

    @Enumerated(EnumType.STRING)
    @ManyToMany
    @JsonIgnore
    @ToString.Exclude
    private Map<Permission.PermissionType, PolicyItem> policies;

}
