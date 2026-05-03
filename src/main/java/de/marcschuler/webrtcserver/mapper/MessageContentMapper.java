package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.message.FileMessageContent;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.data.message.FileMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class MessageContentMapper {
    @SubclassMapping(source = MarkdownMessageContentDTO.class, target = MarkdownMessageContent.class)
    @SubclassMapping(source = FileMessageContentDTO.class, target = FileMessageContent.class)
    public abstract MessageContent mapFromDTO(MessageContentDTO dto);

    @SubclassMapping(source = MarkdownMessageContent.class, target = MarkdownMessageContentDTO.class)
    @SubclassMapping(source = FileMessageContent.class, target = FileMessageContentDTO.class)
    public abstract MessageContentDTO mapToDTO(MessageContent entity);

    public abstract List<MessageContent> mapFromDTO(List<MessageContentDTO> dtos);
}
