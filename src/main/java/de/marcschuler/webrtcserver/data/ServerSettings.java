package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class ServerSettings {
    @Id
    private UUID id;
    @Column(nullable = false)
    private boolean newUsersNeedInvite = false;
}
