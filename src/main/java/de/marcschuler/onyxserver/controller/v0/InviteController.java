package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.dto.data.InviteDTO;
import de.marcschuler.onyxserver.mapper.InviteMapper;
import de.marcschuler.onyxserver.service.InviteService;
import de.marcschuler.onyxserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



    @GetMapping("{serverId}/invites")
    public List<InviteDTO> all(@PathVariable UUID serverId){
        var server = serverService.get(serverId).orElseThrow();
        return inviteService.all(server).stream()
                .map(inviteMapper::mapToDTO)
                .toList();
    }
}
