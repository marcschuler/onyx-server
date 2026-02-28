package de.marcschuler.webrtcserver.data.policy;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@DiscriminatorValue("SIMPLE")
@Entity
@Data
public class AccessPowerPolicy extends Policy{
    private int accessPower;
}
