package de.marcschuler.onyxserver.repository;

import de.marcschuler.onyxserver.data.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
}
