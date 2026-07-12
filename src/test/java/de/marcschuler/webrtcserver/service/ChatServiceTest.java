package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.OnyxTest;
import de.marcschuler.webrtcserver.TestService;
import de.marcschuler.webrtcserver.data.message.FileMessageContent;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.dto.data.FileDTO;
import de.marcschuler.webrtcserver.dto.data.HashDTO;
import de.marcschuler.webrtcserver.dto.data.message.FileMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageCreationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@OnyxTest
@Slf4j
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private TestService testService;

    @Autowired
    private StorageService storageService;

    private static final class NotExistingMessageContentDTO extends MessageContentDTO {
    }

    @Test
    void testCreateInvalidMessageType() {
        var dto = new NotExistingMessageContentDTO();
        assertThrows(IllegalStateException.class, () -> chatService.createMessageContent(dto));
    }

    @Test
    void createMessage() throws Exception {
        var chat = testService.channelLobby().getChat();
        var user = testService.userAdmin();

        var multipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                new ByteArrayInputStream("Hello, World!".getBytes()));
        var file = storageService.uploadFile(multipartFile);

        var hashDto = new HashDTO();
        hashDto.setType(file.getHash().getType());
        hashDto.setHash(file.getHash().getHash());

        var fileDto = new FileDTO();
        fileDto.setId(file.getId());
        fileDto.setFilename(file.getFilename());
        fileDto.setContentType(file.getContentType());
        fileDto.setHash(hashDto);
        fileDto.setCreated(file.getCreated());
        fileDto.setSize(file.getSize());

        var markdownContent = new MarkdownMessageContentDTO();
        markdownContent.setText("Hello **World**!");

        var fileContent = new FileMessageContentDTO();
        fileContent.setFile(fileDto);

        var creationDto = new MessageCreationDTO();
        creationDto.setContent(List.of(markdownContent, fileContent));

        var message = chatService.createMessage(chat, user, creationDto);
        assertNotNull(message.getId());
        assertEquals(2, message.getContent().size());

        var markdownPart = (MarkdownMessageContent) message.getContent().get(0);
        assertEquals("Hello **World**!", markdownPart.getText());

        var filePart = (FileMessageContent) message.getContent().get(1);
        assertEquals(file.getId(), filePart.getFile().getId());
    }

    @Test
    @Transactional
    void createMessageWithReply() {
        var chat = testService.channelLobby().getChat();
        var user = testService.userAdmin();

        var firstContent = new MarkdownMessageContentDTO();
        firstContent.setText("First message");
        var firstDto = new MessageCreationDTO();
        firstDto.setContent(List.of(firstContent));
        var firstMessage = chatService.createMessage(chat, user, firstDto);

        var replyContent = new MarkdownMessageContentDTO();
        replyContent.setText("Reply to first");
        var replyDto = new MessageCreationDTO();
        replyDto.setContent(List.of(replyContent));
        replyDto.setRepliesTo(firstMessage.getId());
        var replyMessage = chatService.createMessage(chat, user, replyDto);

        assertNotNull(replyMessage.getRepliesTo());
        assertEquals(firstMessage.getId(), replyMessage.getRepliesTo().getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50})
    void testChatPagination(int pageSize) {
        var chat = testService.channelLobby().getChat();
        var user = testService.userAdmin();

        long initialCount = chatService.countMessages(chat);
        int newMessages = 51;
        for (int i = 0; i < newMessages; i++) {
            var content = new MarkdownMessageContentDTO();
            content.setText("Message " + i);
            var dto = new MessageCreationDTO();
            dto.setContent(List.of(content));
            chatService.createMessage(chat, user, dto);
        }

        long totalMessages = initialCount + newMessages;
        var page = chatService.page(chat, PageRequest.of(0, pageSize));

        assertEquals(totalMessages, page.getTotalElements());
        assertEquals((int) Math.ceil((double) totalMessages / pageSize), page.getTotalPages());
        assertEquals(pageSize, page.getNumberOfElements());
    }

}
