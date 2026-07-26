package de.marcschuler.onyxserver.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ServerSettings {
    @Id
    @OneToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(nullable = false)
    private boolean newUsersNeedInvite = false;
}
