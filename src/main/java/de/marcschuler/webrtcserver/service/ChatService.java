package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Message;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.repository.ChatRepository;
import de.marcschuler.webrtcserver.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;


    public Optional<Chat> chatById(UUID id) {
        return chatRepository.findById(id);
    }
    
    public Stream<Message> messagesInChat(Chat chat) {
        return messageRepository.getMessagesByChatIs(chat);
    }

    public Message createMessage(Chat chat, User user, String markdown) {
        var content = new MarkdownMessageContent();
        content.setText(markdown);

        var message = new Message();
        message.setChat(chat);
        message.setId(UUID.randomUUID());
        message.setUser(user);
        message.setContent(List.of(content));
        message.setTimestamp(Instant.now());
        messageRepository.save(message);
        //TODO Notify the clients
        return message;
    }
}
