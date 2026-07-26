package de.marcschuler.onyxserver.mapper;

import de.marcschuler.onyxserver.data.message.FileMessageContent;
import de.marcschuler.onyxserver.data.message.MarkdownMessageContent;
import de.marcschuler.onyxserver.data.message.MessageContent;
import de.marcschuler.onyxserver.dto.data.message.FileMessageContentDTO;
import de.marcschuler.onyxserver.dto.data.message.MarkdownMessageContentDTO;
import de.marcschuler.onyxserver.dto.data.message.MessageContentDTO;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
@Slf4j
public abstract class MessageContentMapper {


    /*
        dto to entity
     */
    @SubclassMapping(source = MarkdownMessageContentDTO.class, target = MarkdownMessageContent.class)
    @SubclassMapping(source = FileMessageContentDTO.class, target = FileMessageContent.class)
    @Mapping(target = "id", ignore = true)
    public abstract MessageContent mapFromDTO(MessageContentDTO dto);

    /*
        entity to dto
     */
    @SubclassMapping(source = MarkdownMessageContent.class, target = MarkdownMessageContentDTO.class)
    @SubclassMapping(source = FileMessageContent.class, target = FileMessageContentDTO.class)
    public abstract MessageContentDTO mapToDTO(MessageContent entity);

    public abstract List<MessageContent> mapFromDTO(List<MessageContentDTO> dtos);


    /*
        update
     */
    public MessageContent updateFromDTO(MessageContentDTO dto, @MappingTarget MessageContent entity) {
        if (dto instanceof MarkdownMessageContentDTO d && entity instanceof MarkdownMessageContent e) {
            updateFromDTO(d, e);
        } else if (dto instanceof FileMessageContentDTO d && entity instanceof FileMessageContent e) {
            updateFromDTO(d, e);
        } else {
            throw new IllegalArgumentException(
                    "Incompatible types: " + dto.getClass().getSimpleName()
                            + " -> " + entity.getClass().getSimpleName()
            );
        }
        return entity;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateFromDTO(MarkdownMessageContentDTO dto, @MappingTarget MarkdownMessageContent entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateFromDTO(FileMessageContentDTO dto, @MappingTarget FileMessageContent entity);


}
