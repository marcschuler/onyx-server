package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
}
