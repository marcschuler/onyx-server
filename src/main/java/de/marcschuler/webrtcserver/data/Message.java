package de.marcschuler.webrtcserver.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Message {
    @Id
    private UUID id;
}
