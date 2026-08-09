package de.marcschuler.onyxserver.mapper;

import de.marcschuler.onyxserver.data.message.Message;
import de.marcschuler.onyxserver.dto.data.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        uses = {ServerMapper.class, MessageContentMapper.class})
@Slf4j
public abstract class MessageMapper {

    @Mapping(target = "chatId", source = "message.chat.id")
    public abstract MessageDTO mapToDTO(Message message);

}