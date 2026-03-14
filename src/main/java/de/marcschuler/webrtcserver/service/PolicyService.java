package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.policy.AccessPowerPolicy;
import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import de.marcschuler.webrtcserver.data.policy.SpeLPolicy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import de.marcschuler.webrtcserver.mapper.PolicyMapper;
import de.marcschuler.webrtcserver.repository.PolicyRepository;
import de.marcschuler.webrtcserver.service.policy.*;
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

    private final AccessPowerPolicyChecker accessPowerPolicyChecker;
    private final RolePolicyChecker rolePolicyChecker;
    private final SpelPolicyChecker spelPolicyChecker;

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


    public PolicyResult checkAccess(List<Policy> policies, PolicyCheckerContext context) throws PolicyCheckException {
        var sortedPolicies = policies.stream().sorted().toList();
        for (var policy : sortedPolicies) {
            //noinspection unchecked);
            var policyChecker = (PolicyChecker<Policy>) policyCheckerFromPolicy(policy);

            var result = policyChecker.check(policy, context);
            log.debug("Checked policy with result {}", result);
            if (result.isPresent())
                return result.get() ? PolicyResult.ALLOW : PolicyResult.DENY;
        }
        log.debug("No policies explicitly allowed or denied it.");
        return PolicyResult.DENY;
    }

    private PolicyChecker<? extends Policy> policyCheckerFromPolicy(Policy policy) {
        return switch (policy) {
            case AccessPowerPolicy _ -> accessPowerPolicyChecker;
            case RolePolicy _ -> rolePolicyChecker;
            case SpeLPolicy _ -> spelPolicyChecker;
            default -> throw new IllegalStateException("Unknown policy: " + policy);
        };
    }

    public enum PolicyResult {
        ALLOW,
        DENY
    }
}
