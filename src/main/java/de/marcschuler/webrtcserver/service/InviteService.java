package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.ClientState;
import de.marcschuler.webrtcserver.data.Invite;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.dto.data.InviteDTO;
import de.marcschuler.webrtcserver.error.InviteException;
import de.marcschuler.webrtcserver.mapper.InviteMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.repository.InviteRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketConnectionService;
import de.marcschuler.webrtcserver.webclient.WebClient;
import de.marcschuler.webrtcserver.webclient.messages.client.ClientChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteService {

    private static final String INVALID_INVITE_CODE_MESSAGE = "Invalid invite code";

    private final UserService userService;
    private final InviteRepository inviteRepository;

    private final WebSocketConnectionService webSocketConnectionService;

    private final ServerMapper serverMapper;
    private final InviteMapper inviteMapper;

    public Invite create(InviteDTO inviteDto) {
        var invite = inviteMapper.mapFromDTO(inviteDto);
        if (inviteRepository.existsById(invite.getCode()))
            throw new IllegalStateException("An invite with code " + invite.getCode() + " already exists");

        if (invite.getEndDate().isBefore(LocalDateTime.now()))
            throw new InviteException("The invite has already expired");

        return inviteRepository.save(invite);
    }


    public synchronized Invite enterInviteCode(User user, String code) {
        log.info("Entering invite code for user {}", user);
        if (user.getState() == ClientState.BANNED)
            throw new InviteException("You are banned from this server and cannot use invite codes.");

        var now = LocalDateTime.now();
        var invite = inviteRepository.findById(code).orElseThrow(() -> new InviteException(INVALID_INVITE_CODE_MESSAGE));

        if ((invite.getStartDate() != null && invite.getStartDate().isAfter(now)) || (invite.getEndDate() != null && invite.getEndDate().isBefore(now))) {
            log.debug("User entered an invite code that is not active. Start: {}, End: {}", invite.getStartDate(), invite.getEndDate());
            throw new InviteException(INVALID_INVITE_CODE_MESSAGE);
        }

        if (invite.getMaxUsages() != null && invite.getUsages() == invite.getMaxUsages()) {
            log.debug("Invite code usage limit of {} is reached", invite.getMaxUsages());
            throw new InviteException(INVALID_INVITE_CODE_MESSAGE);
        }

        // only count it if the invite was used this time
        var inviteUsed = false;

        if (user.getState() == ClientState.PENDING_ACCESS) {
            log.info("User is activated");
            user.setState(ClientState.ACTIVE);
            inviteUsed = true;
        }

        if (invite.getGroups() != null && !invite.getGroups().isEmpty()) {
            log.debug("User has groups: {}. Adding groups: {}", user.getGroups(), invite.getGroups());
            var groupsToAdd = invite.getGroups().stream()
                    .filter(g -> !user.getGroups().contains(g))
                    .toList();
            if (!groupsToAdd.isEmpty()) {
                user.getGroups().addAll(groupsToAdd);
                log.info("Added groups {} to user", groupsToAdd);
                inviteUsed = true;
            }
        }

        if (inviteUsed) {
            invite.setUsages(invite.getUsages() + 1);
            inviteRepository.save(invite);
            userService.save(user);
            log.debug("User used the invite, usages: {} of {}", invite.getUsages(), invite.getMaxUsages());

            webSocketConnectionService.clientFromKeyId(user.getId())
                    .map(WebClient::getUser)
                    .ifPresent(u -> webSocketConnectionService.sendToAll(
                            new ClientChangeEvent(serverMapper.mapToDTO(u))
                    ));
            return invite;
        } else {
            throw new InviteException("Invite not applicable: you are already a member with all associated groups.");
        }
    }
}
