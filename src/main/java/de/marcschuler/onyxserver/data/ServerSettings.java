package de.marcschuler.onyxserver.data;

import de.marcschuler.onyxserver.data.converter.PasswordHashConverter;
import de.marcschuler.onyxserver.data.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ServerSettings {
    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(nullable = false)
    private boolean newUsersNeedInvite = false;

    @Convert(converter = PasswordHashConverter.class)
    private String password;

    /**
     * The emojis users may react to messages with. An empty list allows any emoji
     */
    @Convert(converter = StringListConverter.class)
    @Column(nullable = false, length = 8192)
    private List<String> reactionEmojis = new ArrayList<>();
}
