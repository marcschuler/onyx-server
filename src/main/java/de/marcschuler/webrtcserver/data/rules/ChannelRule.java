package de.marcschuler.webrtcserver.data.rules;

import de.marcschuler.webrtcserver.data.Channel;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class ChannelRule {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Channel channel;

    @ManyToMany
    private Map<???,ChannelItemRule> items;


}
