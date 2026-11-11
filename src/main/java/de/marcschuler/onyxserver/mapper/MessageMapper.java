package de.marcschuler.onyxserver.mapper;

import de.marcschuler.onyxserver.data.message.Message;
import de.marcschuler.onyxserver.data.message.MessageReaction;
import de.marcschuler.onyxserver.dto.data.MessageDTO;
import de.marcschuler.onyxserver.dto.data.message.MessageReactionDTO;
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
    @Mapping(target = "temporary", constant = "false")
    public abstract MessageDTO mapToDTO(Message message);

    @Mapping(target = "messageId", source = "reaction.message.id")
    public abstract MessageReactionDTO mapToDTO(MessageReaction reaction);

}