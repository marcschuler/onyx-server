package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.Group;
import de.marcschuler.webrtcserver.data.policy.AccessPowerPolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class AccessPowerPolicyChecker implements PolicyChecker<AccessPowerPolicy> {

    @Override
    public Optional<Boolean> check(AccessPowerPolicy policy, PolicyCheckerContext context) throws PolicyCheckException {
        var value = context.getUser().getGroups().stream()
                .map(Group::getAccessPowers)
                .map(ap -> ap.get(context.getPermissionType()))
                .filter(Objects::nonNull)
                .mapToInt(i -> i)
                .max().orElse(0);
        log.trace("Value is {}. Needed: {}", value, policy.getAccessPower());
        if (value >= policy.getAccessPower()) {
            return Optional.of(true);
        }
        return Optional.empty();
    }
}
