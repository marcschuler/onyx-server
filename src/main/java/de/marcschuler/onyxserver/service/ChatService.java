package de.marcschuler.onyxserver.service;

import de.marcschuler.onyxserver.data.Channel;
import de.marcschuler.onyxserver.data.Chat;
import de.marcschuler.onyxserver.data.User;
import de.marcschuler.onyxserver.data.message.*;
import de.marcschuler.onyxserver.dto.data.message.*;
import de.marcschuler.onyxserver.error.ChatDisabledException;
import de.marcschuler.onyxserver.error.InvalidMessageException;
import de.marcschuler.onyxserver.mapper.MessageContentMapper;
import de.marcschuler.onyxserver.mapper.MessageMapper;
import de.marcschuler.onyxserver.repository.ChannelRepository;
import de.marcschuler.onyxserver.repository.ChatRepository;
import de.marcschuler.onyxserver.repository.MessageRepository;
import de.marcschuler.onyxserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.onyxserver.webclient.messages.chat.ChatMessageEvent;
import de.marcschuler.onyxserver.webclient.messages.chat.MessageDeleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final WebSocketConnectionService webSocketConnectionService;

    private final FileService fileService;
    private final StorageService storageService;

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;
    private final MessageContentMapper messageContentMapper;
    private final ChannelRepository channelRepository;


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

        //if it is a channel, check the chat state
        boolean shouldSave = true;
        var mode = getChannelForChat(chat)
                .map(Channel::getChatMode);
        if (mode.isPresent()) {
            switch (mode.get()) {
                case NONE -> {
                    throw new ChatDisabledException();
                }
                case TEMPORARY -> {
                    shouldSave = false;
                }
                case FULL -> {
                }
            }
        }

        if (shouldSave)
            messageRepository.save(message);

        log.info("New message in chat {}", chat.getId());

        var clients = this.webSocketConnectionService.clientsInteractable();
        this.webSocketConnectionService.send(clients,
                new ChatMessageEvent(chat.getId(), messageMapper.mapToDTO(message)));
        return message;
    }

    public MessageContent createMessageContent(MessageContentDTO messageContent) {
        return switch (messageContent) {
            case TextMessageContentDTO dto -> {
                if (dto.getText().trim().isEmpty())
                    throw new InvalidMessageException("text cannot be empty or whitespace only");
                var c = new TextMessageContent();
                c.setText(dto.getText());
                yield c;
            }
            case MarkdownMessageContentDTO dto -> {
                if (dto.getText().trim().isEmpty())
                    throw new InvalidMessageException("markdown cannot be empty or whitespace only");
                var c = new MarkdownMessageContent();
                c.setText(dto.getText());
                yield c;
            }
            case FileMessageContentDTO dto -> {
                var c = new FileMessageContent();
                var file = fileService.get(dto.getFile().getId()).orElseThrow();
                c.setFile(file);
                yield c;
            }
            case null, default -> throw new IllegalStateException("Message Content " + messageContent + " not found");
        };
    }

    public Optional<Channel> getChannelForChat(Chat chat) {
        return channelRepository.findChannelByChatIs(chat);
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

    public void deleteMessage(Chat chat, Message message) {
        chat.getMessages().removeIf(m -> m.getId().equals(message.getId()));
        chatRepository.save(chat);
        this.webSocketConnectionService.sendToAll(new MessageDeleteEvent(message.getId()));
    }
}
