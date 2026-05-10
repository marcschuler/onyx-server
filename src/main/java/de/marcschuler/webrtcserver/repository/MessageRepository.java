package de.marcschuler.webrtcserver.repository;

import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.stream.Stream;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Stream<Message> getMessagesByChatIs(Chat chat);

    Page<Message> findMessagesByChatIs(Chat chat, Pageable pageable);

    long countMessageByChatIs(Chat chat);
}
