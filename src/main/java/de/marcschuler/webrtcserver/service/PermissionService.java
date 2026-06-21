package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.config.SecurityConfig;
import de.marcschuler.webrtcserver.data.*;
import de.marcschuler.webrtcserver.data.permission.Permission;
import de.marcschuler.webrtcserver.data.permission.PermissionType;
import de.marcschuler.webrtcserver.error.webclient.PermissionDeniedException;
import de.marcschuler.webrtcserver.webclient.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

import static de.marcschuler.webrtcserver.service.PermissionService.PermissionState.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    /*
        Check Access for AuthenticatedUsers
     */
    public void checkControllerAccess(@Nullable Channel channel, @NonNull PermissionType type) {
        checkControllerAccess(channel != null ? channel.getSection() : null, channel, type);
    }

    public void checkControllerAccess(@Nullable Section section, @Nullable Channel channel, @NonNull PermissionType type) {
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null)
            throw new PermissionDeniedException("Client is not authenticated", type);

        var principal = (SecurityConfig.AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkAccess(principal.user(), section, channel, type);
    }

    /*
        Check Access for WebClients
     */

    public void checkClientAccess(@NonNull WebClient webClient, @Nullable Channel channel, @NonNull PermissionType type) {
        checkClientAccess(webClient, channel != null ? channel.getSection() : null, channel, type);
    }

    void checkClientAccess(@NonNull WebClient webClient, @Nullable Section section, @Nullable Channel channel, @NonNull PermissionType type) {
        if (webClient.getUser() == null)
            throw new PermissionDeniedException("WebClient is not authenticated", type);
        checkAccess(webClient.getUser(), section, channel, type);
    }


    /**
     * Checks wether a user in a specific section and channel can do an action
     *
     * @param user    the user to check
     * @param section the section, may be null
     * @param channel the channel of the section, may be null
     * @param type
     */
    public void checkAccess(@NonNull User user, @Nullable Section section, @Nullable Channel channel, @NonNull PermissionType type) {
        if (user.getState() == ClientState.BANNED) {
            log.warn("User {} has been banned", user.getUsername());
            throw new PermissionDeniedException("User is banned", null);
        }
        if (user.getState() == ClientState.PENDING_ACCESS) {
            throw new PermissionDeniedException("User is pending access", null);
        }

        var groups = buildGroupContext(user, section, channel);

        for (var group : groups) {
            var permissions = group.getPermissions().stream()
                    .sorted(Comparator.comparingInt(Permission::getPriority))
                    .toList();
            for (var permission : permissions) {
                var state = checkAccessForSinglePermission(permission, section, channel, type);
                if (state == ALLOW) {
                    log.debug("Permission {} has access for section {} and channel {}", permission, section, channel);
                    return;
                }
                if (state == DENY) {
                    log.debug("Permission {} denied by group {} for  access for section {} and channel {}", group, permission, section, channel);
                    throw new PermissionDeniedException("Permission denied for Group '" + group.getName() + "'", type);
                }
            }
        }
        log.debug("Permissions have nothing defined for section {} and channel {}", section, channel);
        throw new PermissionDeniedException("Permission denied", type);
    }

    public PermissionState checkAccessForSinglePermission(Permission permission, Section section, Channel channel, PermissionType type) {
        log.trace("Checking Permission {} for section {} and channel {}", permission, section, channel);
        // Check if the permission has the wanted type or its parent
        while (!permission.getPermissions().contains(type)) {
            var root = type.root();
            if (root == type) { //reached end of tree
                return UNKNOWN;
            } else {
                type = root;
            }
        }
        log.trace("Checking for type {}", type);

        // Check if we are limited to channel
        if (permission.getLimitedToChannel() != null && !permission.getLimitedToChannel().isEmpty() &&
                !permission.getLimitedToChannel().contains(channel)) {
            log.trace("Limited to channel, but channel {} is not in list", channel);
            return UNKNOWN;
        }

        // Check if we are limited to sections
        if (permission.getLimitedToSection() != null && !permission.getLimitedToSection().isEmpty() &&
                !permission.getLimitedToSection().contains(section)) {
            log.trace("Limited to section, but section {} is not in list", section);
            return UNKNOWN;
        }

        // check if inverted
        if (permission.isInverted()) {
            log.trace("Permission denied");
            return DENY;
        } else {
            log.trace("Permission allowed");
            return ALLOW;
        }
    }

    public Set<Group> buildGroupContext(@NonNull User user, @Nullable Section section, @Nullable Channel channel) {
        var groups = new TreeSet<Group>();

        groups.addAll(user.getGroups());

        if (section != null && user.getSectionGroups() != null) {
            var sectionGroups = user.getSectionGroups().stream()
                    .filter(s -> s.getSection().equals(section))
                    .flatMap(s -> s.getGroups().stream())
                    .toList();
            groups.addAll(sectionGroups);
        }
        if (channel != null && user.getChannelGroups() != null) {
            var channelGroups = user.getChannelGroups().stream()
                    .filter(s -> s.getChannel().equals(channel))
                    .flatMap(s -> s.getGroups().stream())
                    .toList();
            groups.addAll(channelGroups);
        }
        return groups;
    }

    public enum PermissionState {
        ALLOW,
        DENY,
        UNKNOWN
    }

}
