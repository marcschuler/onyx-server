package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.data.permission.PermissionType;
import de.marcschuler.onyxserver.dto.data.InviteDTO;
import de.marcschuler.onyxserver.mapper.InviteMapper;
import de.marcschuler.onyxserver.service.InviteService;
import de.marcschuler.onyxserver.service.PermissionService;
import de.marcschuler.onyxserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/server/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InviteController {

    private final ServerService serverService;
    private final InviteService inviteService;
    private final InviteMapper inviteMapper;

    private final PermissionService permissionService;

    @GetMapping("{serverId}/invites")
    public List<InviteDTO> all(@PathVariable UUID serverId){
        var server = serverService.get(serverId).orElseThrow();
        return inviteService.all(server).stream()
                .map(inviteMapper::mapToDTO)
                .toList();
    }

    @PostMapping("{serverId}/invites")
    public InviteDTO create(@PathVariable UUID serverId, @RequestBody InviteDTO inviteDTO) {
        permissionService.checkControllerAccess(null, PermissionType.SERVER_INVITE);

        var server = serverService.get(serverId).orElseThrow();
        var invite = inviteService.create(inviteDTO,server);
        return inviteMapper.mapToDTO(invite);
    }

    @PutMapping("{serverId}/invites/{inviteId}")
    public InviteDTO edit(@PathVariable UUID serverId, @PathVariable String inviteId, @RequestBody InviteDTO inviteDTO) {
        permissionService.checkControllerAccess(null, PermissionType.SERVER_INVITE);

        serverService.get(serverId).orElseThrow();
        var invite = inviteService.get(inviteId).orElseThrow();
        inviteService.edit(invite, inviteDTO);
        return inviteMapper.mapToDTO(invite);
    }

    @DeleteMapping("{serverId}/invites/{inviteId}")
    public void delete(@PathVariable UUID serverId, @PathVariable String inviteId) {
        permissionService.checkControllerAccess(null, PermissionType.SERVER_INVITE);

        var server = serverService.get(serverId).orElseThrow();
        var invite = inviteService.get(inviteId).orElseThrow();
        inviteService.delete(invite);
    }
}
