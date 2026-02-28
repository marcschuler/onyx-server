package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePolicyChecker implements PolicyChecker<RolePolicy> {

    @Override
    public Optional<Boolean> check(RolePolicy policy, PolicyCheckerContext context) throws PolicyCheckException {
        var wantedIds = policy.getIds();

        var user = context.getUser();
        var givenIds = switch (policy.getOperand()) {
            case GROUP -> user.getGroups().stream()
                    .map(Group::getId).collect(Collectors.toSet());
            case USER -> new HashSet<>(List.of(user.getId()));
        };

        switch (policy.getOperator()) {
            case IN -> {
                return Optional.of(
                        wantedIds.stream()
                                .anyMatch(givenIds::contains)
                );
            }
            case NOT_IN -> {
                return Optional.of(
                        wantedIds.stream()
                                .noneMatch(givenIds::contains)
                );
            }
            case IN_ALL -> {
                return Optional.of(givenIds.equals(wantedIds));
            }
            default -> {
                throw new PolicyCheckException("No operator set");
            }
        }

    }
}
