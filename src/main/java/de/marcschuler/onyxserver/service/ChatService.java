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
import de.marcschuler.onyxserver.webclient.messages.chat.MessageReactionAddEvent;
import de.marcschuler.onyxserver.webclient.messages.chat.MessageReactionRemoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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

    public Message createMessage(Chat chat, User user, MessageCreationDTO messageCreationDTO) {
        var message = new Message();
        message.setChat(chat);
        message.setId(UUID.randomUUID());
        message.setUser(user);
        message.setContent(
                messageCreationDTO.getContent().stream()
                        .map(this::createMessageContent).toList());
        message.setTimestamp(Instant.now());
        if (messageCreationDTO.getRepliesTo() != null)
            message.setRepliesTo(messageById(messageCreationDTO.getRepliesTo()).orElseThrow());

        //if it is a channel, check the chat state
        boolean temporary = false;
        var mode = getChannelForChat(chat)
                .map(Channel::getChatMode);
        if (mode.isPresent()) {
            switch (mode.get()) {
                case NONE -> {
                    throw new ChatDisabledException();
                }
                case TEMPORARY -> {
                    temporary = true;
                }
                case FULL -> {
                }
            }
        }

        if (!temporary)
            messageRepository.save(message);


        log.info("New message in chat {}", chat.getId());

        var messageDto = messageMapper.mapToDTO(message);
        if (temporary)
            messageDto.setTemporary(true);
        var chatMessageEvent = new ChatMessageEvent(chat.getId(), messageDto);
        this.webSocketConnectionService.clients()
                .forEach(client -> client.send(chatMessageEvent));
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

    public Optional<Message> messageById(UUID id) {
        return messageRepository.findById(id);
    }

    public void deleteMessage(Chat chat, Message message) {
        chat.getMessages().removeIf(m -> m.getId().equals(message.getId()));
        chatRepository.save(chat);
        this.webSocketConnectionService.sendToAll(new MessageDeleteEvent(message.getId()));
    }

    /**
     * Adds a reaction of a user to a message. If the user already reacted with this emoji, the existing reaction is returned
     *
     * @param allowedEmojis the emojis allowed on this server, an empty list allows any emoji
     */
    @Transactional
    public MessageReaction addReaction(Message message, User user, String emoji, List<String> allowedEmojis) {
        if (emoji == null || emoji.isBlank())
            throw new InvalidMessageException("emoji cannot be empty");
        emoji = emoji.strip();
        if (emoji.length() > MessageReaction.MAX_EMOJI_LENGTH)
            throw new InvalidMessageException("emoji is too long");
        if (!allowedEmojis.isEmpty() && !allowedEmojis.contains(emoji))
            throw new InvalidMessageException("emoji is not allowed on this server");

        final var e = emoji;
        var existing = message.getReactions().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()) && r.getEmoji().equals(e))
                .findAny();
        if (existing.isPresent())
            return existing.get();

        var reaction = new MessageReaction();
        reaction.setMessage(message);
        reaction.setUser(user);
        reaction.setEmoji(emoji);
        reaction.setTimestamp(Instant.now());
        message.getReactions().add(reaction);
        messageRepository.saveAndFlush(message);

        // the saved reaction now has its generated id
        var saved = message.getReactions().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()) && r.getEmoji().equals(e))
                .findAny().orElseThrow();
        webSocketConnectionService.sendToAll(
                new MessageReactionAddEvent(message.getChat().getId(), messageMapper.mapToDTO(saved)));
        return saved;
    }

    /**
     * Removes the reaction of a user with the given emoji from a message, if it exists
     */
    @Transactional
    public void removeReaction(Message message, User user, String emoji) {
        var e = emoji == null ? "" : emoji.strip();
        var reaction = message.getReactions().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()) && r.getEmoji().equals(e))
                .findAny();
        if (reaction.isEmpty())
            return;
        message.getReactions().remove(reaction.get());
        messageRepository.save(message);
        webSocketConnectionService.sendToAll(
                new MessageReactionRemoveEvent(message.getChat().getId(), message.getId(), reaction.get().getId()));
    }
}
