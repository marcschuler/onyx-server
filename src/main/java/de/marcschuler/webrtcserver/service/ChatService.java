package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.Message;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.mapper.MessageMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.ChatRepository;
import de.marcschuler.webrtcserver.repository.MessageRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.messages.chat.IncomeMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final WebSocketService webSocketService;
    private final WebSocketConnectionService webSocketConnectionService;

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    private final ServerMapper serverMapper;
    private final MessageMapper messageMapper;


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

        log.info("New message in chat {}", chat.getId());

        var clients = this.webSocketService.getClientsInChat(chat);
        this.webSocketConnectionService.sendToClients(clients,
                new IncomeMessageEvent(chat.getId(), messageMapper.mapToDTO(message)));
        return message;
    }
}
