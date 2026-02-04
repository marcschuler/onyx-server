package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@DiscriminatorValue("SPEL")
@Entity
@Data
public class SpeLPolicy extends Policy{

    private String spel;
}
