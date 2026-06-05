package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.Channel;
import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.Section;
import de.marcschuler.webrtcserver.data.permission.Permission;
import de.marcschuler.webrtcserver.dto.GroupCreateDTO;
import de.marcschuler.webrtcserver.dto.PermissionDTO;
import de.marcschuler.webrtcserver.dto.data.GroupDTO;
import de.marcschuler.webrtcserver.service.ChannelService;
import de.marcschuler.webrtcserver.service.SectionService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class GroupMapper {

    @Autowired
    @Lazy
    protected ChannelService channelService;
    @Autowired
    @Lazy
    protected SectionService sectionService;

    public abstract List<GroupDTO> mapToDTO(Set<Group> groups);

    @Mapping(target = "permissions", qualifiedByName = "permissionFromDTO")
    public abstract Group mapFromDTO(GroupDTO groupDTO);

    public abstract Group mapFromDTO(GroupCreateDTO groupCreateDTO);

    @Mapping(target = "permissions", qualifiedByName = "permissionFromDTO")
    public abstract Group update(@MappingTarget Group group, GroupDTO groupDTO);

    // Single item — Named here, MapStruct auto-iterates for the List
    @Named("permissionFromDTO")
    @Mapping(target = "limitedToSection", source = "limitedToSection", qualifiedByName = "uuidsToSections")
    @Mapping(target = "limitedToChannel", source = "limitedToChannel", qualifiedByName = "uuidsToChannels")
    public abstract Permission permissionFromDTO(PermissionDTO dto);

    // Keep the unnamed one only if needed elsewhere without qualifier
    @Mapping(target = "limitedToSection", source = "limitedToSection", qualifiedByName = "uuidsToSections")
    @Mapping(target = "limitedToChannel", source = "limitedToChannel", qualifiedByName = "uuidsToChannels")
    public abstract Permission mapFromDTO(PermissionDTO dto);

    @Mapping(target = "limitedToSection", source = "limitedToSection", qualifiedByName = "sectionsToUuids")
    @Mapping(target = "limitedToChannel", source = "limitedToChannel", qualifiedByName = "channelsToUuids")
    public abstract PermissionDTO mapToDTO(Permission permission);

    public abstract GroupDTO mapToDTO(Group group);

    // --- helpers ---

    @Named("uuidsToSections")
    protected Set<Section> uuidsToSections(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return Collections.emptySet();
        return uuids.stream()
                .map(sectionService::get)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());
    }

    @Named("uuidsToChannels")
    protected Set<Channel> uuidsToChannels(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) return Collections.emptySet();
        return uuids.stream()
                .map(channelService::get)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());
    }

    @Named("sectionsToUuids")
    protected List<UUID> sectionsToUuids(Set<Section> sections) {
        if (sections == null || sections.isEmpty()) return Collections.emptyList();
        return sections.stream()
                .map(Section::getId)
                .toList();
    }

    @Named("channelsToUuids")
    protected List<UUID> channelsToUuids(Set<Channel> channels) {
        if (channels == null || channels.isEmpty()) return Collections.emptyList();
        return channels.stream()
                .map(Channel::getId)
                .toList();
    }
}