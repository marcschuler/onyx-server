package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.message.Message;
import de.marcschuler.webrtcserver.dto.data.MessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        uses = {ServerMapper.class, MessageContentMapper.class})
public abstract class MessageMapper {

    public abstract MessageDTO mapToDTO(Message message);


}