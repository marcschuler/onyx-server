package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
