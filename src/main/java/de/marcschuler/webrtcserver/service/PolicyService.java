package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.data.policy.SimplePolicy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import de.marcschuler.webrtcserver.mapper.PolicyMapper;
import de.marcschuler.webrtcserver.repository.PolicyRepository;
import de.marcschuler.webrtcserver.service.policy.SimplePolicyChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

    private final PolicyRepository policyRepository;

    private final PolicyMapper policyMapper;

    public Policy create(PolicyWriteDTO policyWriteDTO) {
        var policy = policyMapper.mapFromDTO(policyWriteDTO);
        return policyRepository.save(policy);
    }

    public Optional<Policy> get(UUID id) {
        return policyRepository.findById(id);
    }

    public void edit(Policy policy, PolicyWriteDTO policyWriteDTO) {
        policyMapper.update(policy, policyWriteDTO);
    }

    public void delete(Policy policy) {
        policyRepository.delete(policy);
    }


    public EvaluationContext buildContext(Map<String, Object> map) {
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding()
                .withInstanceMethods()
                .build();
        map.forEach(context::setVariable);
        return context;
    }


    public PolicyResult canAccessPerRule(List<Policy> policies, Map<String, Object> map, PolicyResult defaultValue) throws PolicyCheckException {
        var sortedPolicies = policies.stream().sorted().toList();
        for (var policy : sortedPolicies) {
            var result = new SimplePolicyChecker().check((SimplePolicy) policy, map);
            if (result.isPresent())
                return result.get() ? PolicyResult.ALLOW : PolicyResult.DENY;
        }
        return defaultValue;
    }

    public enum PolicyResult {
        ALLOW,
        DENY
    }
}
