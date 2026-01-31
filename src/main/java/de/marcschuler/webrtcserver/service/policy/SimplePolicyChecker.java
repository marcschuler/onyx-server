package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.data.policy.SimplePolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimplePolicyChecker implements PolicyChecker<SimplePolicy> {

    @Override
    public Optional<Boolean> check(SimplePolicy policy, Map<String, Object> context) throws PolicyCheckException {
        var wantedIds = policy.getIds();

        var user = (User) context.get(PolicyChecker.CONTEXT_USER);
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
