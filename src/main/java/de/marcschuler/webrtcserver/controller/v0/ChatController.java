package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.dto.data.MessageWriteDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageCreationDTO;
import de.marcschuler.webrtcserver.mapper.MessageMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ChatService;
import de.marcschuler.webrtcserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/chat",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    private final MessageMapper messageMapper;
    private final ServerMapper serverMapper;

    @GetMapping("{id}/message")
    @Transactional(readOnly = true)
    public List<MessageDTO> messages(@PathVariable UUID id) {
        var chat = chatService.chatById(id).orElseThrow();
        return chatService.messagesInChat(chat)
                .map(messageMapper::mapToDTO)
                .toList();
    }

    @PostMapping("{id}/message")
    public MessageDTO message(@PathVariable UUID id, @RequestBody MessageCreationDTO message, Principal principal) {
        var chat = chatService.chatById(id).orElseThrow();
        var user = userService.findById(principal.getName()).orElseThrow();
        var m = chatService.createMessage(chat, user, message.getMarkdown());
        return messageMapper.mapToDTO(m);
    }
}
