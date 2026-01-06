package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.Message;
import de.marcschuler.webrtcserver.data.message.MarkdownMessageContent;
import de.marcschuler.webrtcserver.data.message.MessageContent;
import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import de.marcschuler.webrtcserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ServerMapper.class)
public abstract class MessageMapper {


    public abstract MessageDTO mapToDTO(Message message);

    @SubclassMapping(source = MarkdownMessageContent.class, target = MarkdownMessageContentDTO.class)
    public abstract MessageContentDTO mapToDTO(MessageContent entity);

   // @SubclassMapping(source = MarkdownMessageContentDTO.class, target = MarkdownMessageContent.class)
   // public abstract MessageContent mapFromDTO(MessageContentDTO dto);
}
