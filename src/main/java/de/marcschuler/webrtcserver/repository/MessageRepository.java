package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.stream.Stream;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Stream<Message> getMessagesByChatIs(Chat chat);
}
