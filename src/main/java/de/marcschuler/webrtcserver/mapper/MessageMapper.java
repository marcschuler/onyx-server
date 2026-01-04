package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public abstract class MessageMapper {

    @SubclassMapping(source = MarkdownMessageContent.class, target = MarkdownMessageContentDTO.class)
    public abstract MessageContentDTO mapToDTO(MessageContent entity);

    @SubclassMapping(source = MarkdownMessageContentDTO.class, target = MarkdownMessageContent.class)
    public abstract MessageContent mapFromDTO(MessageContentDTO dto);
}
