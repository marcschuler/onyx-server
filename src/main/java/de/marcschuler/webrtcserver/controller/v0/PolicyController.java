package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.ChannelDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.mapper.PolicyMapper;
import de.marcschuler.webrtcserver.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/policy",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    private final PolicyMapper policyMapper;

    @PostMapping
    public PolicyDTO create(@RequestBody PolicyWriteDTO policyWriteDTO) {
        var policy = policyService.create(policyWriteDTO);
        return policyMapper.mapToDTO(policy);
    }

    @PutMapping("{id}")
    public PolicyDTO edit(@PathVariable UUID id, @RequestBody PolicyWriteDTO policyWriteDTO) {
        var policy = policyService.get(id).orElseThrow();
        policyService.edit(policy, policyWriteDTO);
        return policyMapper.mapToDTO(policy);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        var channel = policyService.get(id).orElseThrow();
        policyService.delete(channel);
    }
}
