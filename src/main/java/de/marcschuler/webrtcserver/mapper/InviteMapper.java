package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.Invite;
import de.marcschuler.webrtcserver.dto.data.InviteDTO;
import de.marcschuler.webrtcserver.service.GroupService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class InviteMapper {

    @Autowired
    @Lazy
    protected GroupService groupService;

    @Mapping(target = "groups", source = "groups", qualifiedByName = "uuidsToGroups")
    public abstract Invite mapFromDTO(InviteDTO dto);

    @Named("uuidsToGroups")
    protected List<Group> uuidsToSections(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return List.of();
        return uuids.stream()
                .map(groupService::get)
                .map(Optional::orElseThrow)
                .collect(Collectors.toList());
    }
}
