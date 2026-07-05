package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.message.Message;
import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        uses = {ServerMapper.class, MessageContentMapper.class})
@Slf4j
public abstract class MessageMapper {

    public abstract MessageDTO mapToDTO(Message message);

}