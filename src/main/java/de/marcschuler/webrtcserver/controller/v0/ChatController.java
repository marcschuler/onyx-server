package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.config.SecurityConfig;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageCreationDTO;
import de.marcschuler.webrtcserver.mapper.MessageMapper;

import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.ChatService;
import de.marcschuler.webrtcserver.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/chat",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChannelService channelService;
    private final PermissionService permissionService;

    private final MessageMapper messageMapper;

    @GetMapping("{id}/messages")
    @Transactional(readOnly = true)
    public Page<MessageDTO> messages(@PathVariable UUID id, Pageable page) {
        var chat = chatService.chatById(id).orElseThrow();

        var channel = channelService.getByChat(chat);
        channel.ifPresent(c -> permissionService.checkControllerAccess(c, PermissionType.CHANNEL_CHAT_READ));

        return chatService.page(chat, page)
                .map(messageMapper::mapToDTO);
    }

    @GetMapping("{id}/messages/latest")
    public Page<MessageDTO> messagesLatest(@PathVariable UUID id, @RequestParam(defaultValue = "50") int size) {
        var chat = chatService.chatById(id).orElseThrow();

        var channel = channelService.getByChat(chat);
        channel.ifPresent(c -> permissionService.checkControllerAccess(c, PermissionType.CHANNEL_CHAT_READ));

        var count = chatService.countMessages(chat);
        var lastPage = (int) Math.max(0, (count - 1) / size);
        var page = PageRequest.of(lastPage, size);
        return chatService.page(chat, page)
                .map(messageMapper::mapToDTO);
    }

    @PostMapping("{id}/message")
    public MessageDTO message(@PathVariable UUID id, @RequestBody MessageCreationDTO message, @AuthenticationPrincipal SecurityConfig.AuthenticatedUser authUser) {
        var chat = chatService.chatById(id).orElseThrow();

        var channel = channelService.getByChat(chat);
        channel.ifPresent(c -> permissionService.checkControllerAccess(c, PermissionType.CHANNEL_CHAT_WRITE));

        var user = authUser.user();
        var m = chatService.createMessage(chat, user, message);
        return messageMapper.mapToDTO(m);
    }
}
