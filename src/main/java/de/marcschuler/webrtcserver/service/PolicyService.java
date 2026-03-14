package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.policy.*;
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

import java.util.*;

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


    public void checkAccess(PolicyItem policyItem, PolicyCheckerContext context) throws PolicyCheckException {
        checkAccess(policyItem.getPolicies(), context);
    }

    /**
     * Checks wether a user has access to a specific resource
     * @param policies a list of policies the resource has
     * @param context the context
     * @throws PolicyCheckException if the user is not allowed to access the resource
     */
    public void checkAccess(Collection<Policy> policies, PolicyCheckerContext context) throws PolicyCheckException {
        var sortedPolicies = policies.stream().sorted().toList();

        if (sortedPolicies.isEmpty())
            throw new PolicyCheckException("No policies defined", context.getPermissionType());

        for (var policy : sortedPolicies) {
            //noinspection unchecked);
            var policyChecker = (PolicyChecker<Policy>) policyCheckerFromPolicy(policy);

            var result = policyChecker.check(policy, context);
            log.debug("Checked policy with result {}", result);
            if (result.isPresent() && !result.get())
                throw new PolicyCheckException("Policy '" + policy.getName() + "' denied access", context.getPermissionType());
        }
        log.debug("No policies explicitly allowed or denied it.");
        throw new PolicyCheckException("No policies explicitly allowed or denied.", context.getPermissionType());
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
