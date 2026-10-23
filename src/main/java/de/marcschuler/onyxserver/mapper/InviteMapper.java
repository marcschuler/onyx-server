package de.marcschuler.onyxserver.mapper;

import de.marcschuler.onyxserver.data.Group;
import de.marcschuler.onyxserver.data.Invite;
import de.marcschuler.onyxserver.data.Server;
import de.marcschuler.onyxserver.dto.data.InviteDTO;
import de.marcschuler.onyxserver.dto.data.SimpleGroupDTO;
import de.marcschuler.onyxserver.service.GroupService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        uses = {GroupMapper.class})
public abstract class InviteMapper {

    @Autowired
    @Lazy
    protected GroupService groupService;

    @Mapping(target = "groups", source = "dto.groups", qualifiedByName = "simpleGroupsToGroups")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "server", source = "server")
    public abstract Invite mapFromDTO(InviteDTO dto, Server server);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "server", ignore = true)
    @Mapping(target = "usages", ignore = true)
    @Mapping(target = "groups", source = "groups", qualifiedByName = "simpleGroupsToGroups")
    public abstract Invite update(@MappingTarget Invite invite, InviteDTO dto);

    @Named("simpleGroupsToGroups")
    protected List<Group> uuidsToSections(List<SimpleGroupDTO> simpleGroups) {
        if (simpleGroups == null || simpleGroups.isEmpty()) return List.of();
        return simpleGroups.stream()
                .map(u -> groupService.get(u.getId()))
                .map(Optional::orElseThrow)
                .collect(Collectors.toList());
    }

    public abstract InviteDTO mapToDTO(Invite invite);

}
