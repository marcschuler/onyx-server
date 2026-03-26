package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ServerMapper.class)
public abstract class GroupMapper {

    public abstract List<GroupDTO> mapToDTO(Set<Group> groups);
}
