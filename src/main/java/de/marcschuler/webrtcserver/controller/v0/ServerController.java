package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ServerDTO;
import de.marcschuler.webrtcserver.dto.data.message.MessageContentDTO;
import de.marcschuler.webrtcserver.mapper.MessageContentMapper;
import de.marcschuler.webrtcserver.mapper.ServerMapper;
import de.marcschuler.webrtcserver.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/server/",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    private final ServerMapper serverMapper;
    private final MessageContentMapper messageContentMapper;

    @PutMapping("{serverId}")
    public ServerDTO edit(@PathVariable UUID serverId, @RequestBody ServerDTO serverDto) {
        var server = serverService.get(serverId).orElseThrow();
        server = serverService.update(server, serverDto);
        return serverMapper.mapToDTO(server);
    }

    @GetMapping("{id}")
    public ServerDTO get(@PathVariable UUID id) {
        ServerDTO serverDTO = serverMapper.mapToDTO(serverService.get(id).orElseThrow());
        return serverDTO;
    }

    @RestController
    @RequestMapping(value = "/v0/server/{id}/description",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiredArgsConstructor
    public class ServerDescriptionController {

        @PostMapping
        public MessageContentDTO create(@RequestBody MessageContentDTO messageDto, @PathVariable UUID id) {
            var server = serverService.get(id).orElseThrow();
            var content = serverService.createDescription(server, messageDto);
            return messageContentMapper.mapToDTO(content);
        }

        @PutMapping("{descriptionId}")
        public MessageContentDTO edit(@RequestBody MessageContentDTO messageDto, @PathVariable UUID id, @PathVariable UUID descriptionId) {
            var server = serverService.get(id).orElseThrow();
            var content = serverService.descriptionFromId(server, descriptionId).orElseThrow();
            content = serverService.updateDescription(server, content, messageDto);
            return messageContentMapper.mapToDTO(content);
        }

        @PutMapping("{descriptionId}/order/{newOrder}")
        public void order(@PathVariable UUID id, @PathVariable UUID descriptionId, @PathVariable int newOrder) {
            var server = serverService.get(id).orElseThrow();
            var content = serverService.descriptionFromId(server, descriptionId).orElseThrow();
            serverService.orderDescription(server, content, newOrder);
        }

        @DeleteMapping("{descriptionId}")
        public void delete(@PathVariable UUID id, @PathVariable UUID descriptionId) {
            var server = serverService.get(id).orElseThrow();
            serverService.deleteDescription(server, descriptionId);
        }


    }


}
