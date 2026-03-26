package de.marcschuler.webrtcserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel implements Policyable {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, length = 32)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, optional = false,fetch =  FetchType.EAGER)
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
    private Map<Permission.PermissionType, de.marcschuler.webrtcserver.data.policy.PolicyItem> policies;

}
