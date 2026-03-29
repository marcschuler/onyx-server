package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.mapper.PolicyMapper;
import de.marcschuler.webrtcserver.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v0/policy",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    private final PolicyMapper policyMapper;

    @GetMapping
    public List<PolicyDTO> policies(){
        return policyService.all().stream()
                .map(policyMapper::mapToDTO)
                .toList();
    }

    @PostMapping
    public PolicyDTO create(@RequestBody PolicyDTO policyDTO) {
        var policy = policyService.create(policyDTO);
        return policyMapper.mapToDTO(policy);
    }

    @PutMapping("{id}")
    public PolicyDTO edit(@PathVariable UUID id, @RequestBody PolicyDTO policyDTO) {
        var policy = policyService.get(id).orElseThrow();
        policyService.edit(policy, policyDTO);
        return policyMapper.mapToDTO(policy);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        var channel = policyService.get(id).orElseThrow();
        policyService.delete(channel);
    }
}
