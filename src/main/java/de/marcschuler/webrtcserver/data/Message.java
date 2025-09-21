package de.marcschuler.webrtcserver.data;

import de.marcschuler.webrtcserver.dto.SignedContent;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Message {
    @Id
    private UUID id;
}
