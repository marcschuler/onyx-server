package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public final class SpeLPolicy extends Policy{

    private String spel;
}
