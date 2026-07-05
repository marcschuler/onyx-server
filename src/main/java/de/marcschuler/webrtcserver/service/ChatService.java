package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.Chat;
import de.marcschuler.webrtcserver.data.message.Message;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.message.FileMessageContent;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.dto.data.message.FileMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageCreationDTO;
import de.marcschuler.webrtcserver.error.InvalidMessageException;
import de.marcschuler.webrtcserver.mapper.MessageContentMapper;
import de.marcschuler.webrtcserver.mapper.MessageMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.ChatRepository;
import de.marcschuler.webrtcserver.repository.MessageRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import de.marcschuler.webrtcserver.webclient.messages.chat.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final WebSocketConnectionService webSocketConnectionService;

    private final StorageService storageService;

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;
    private final MessageContentMapper messageContentMapper;


    public Optional<Chat> chatById(UUID id) {
        return chatRepository.findById(id);
    }

    public Stream<Message> messagesInChat(Chat chat) {
        return messageRepository.getMessagesByChatIs(chat);
    }

    public Message createMessage(Chat chat, User user, MessageCreationDTO messageDto) {
        var message = new Message();
        message.setChat(chat);
        message.setId(UUID.randomUUID());
        message.setUser(user);
        message.setContent(
                messageDto.getContent().stream()
                        .map(this::createMessageContent).toList());
        message.setTimestamp(Instant.now());
        if (messageDto.getRepliesTo() != null)
            message.setRepliesTo(messageById(messageDto.getRepliesTo()).orElseThrow());

        messageRepository.save(message);

        log.info("New message in chat {}", chat.getId());

        var clients = this.webSocketConnectionService.clientsInteractable();
        this.webSocketConnectionService.send(clients,
                new ChatMessageEvent(chat.getId(), messageMapper.mapToDTO(message)));
        return message;
    }

    public MessageContent createMessageContent(MessageContentDTO messageContent) {
        return switch (messageContent) {
            case MarkdownMessageContentDTO dto -> {
                if (dto.getText().trim().isEmpty())
                    throw new InvalidMessageException("markdown cannot be empty or whitespace only");
                var c = new MarkdownMessageContent();
                c.setText(dto.getText());
                yield c;
            }
            case FileMessageContentDTO dto -> {
                var c = new FileMessageContent();
                var file = storageService.get(dto.getFile().getId()).orElseThrow();
                c.setFile(file);
                yield c;
            }
            case null, default -> throw new IllegalStateException("Message Content " + messageContent + " not found");
        };
    }

    public Page<Message> page(Chat chat, Pageable page) {
        return messageRepository.findMessagesByChatIs(chat, page);
    }

    public long countMessages(Chat chat) {
        return messageRepository.countMessageByChatIs(chat);
    }

    public MessageContent updateMessageContent(MessageContent messageContent, MessageContentDTO messageDto) {
        return messageContentMapper.updateFromDTO(messageDto, messageContent);
    }

    public Optional<Message> messageById(UUID id) {
        return messageRepository.findById(id);
    }
}
