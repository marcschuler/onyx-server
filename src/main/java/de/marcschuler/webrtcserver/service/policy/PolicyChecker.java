package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;

import java.util.Map;
import java.util.Optional;


public interface PolicyChecker<T extends Policy> {

    /**
     * Checks if a user has the permission to do something
     *
     * @param policy  the policy to check against
     * @param context context
     * @return Either Optional(true) if the user has access, Optional(false) if the user explicitly does not have access or Optional(empty) if the next rule should decide. In most cases,Optional(false) is not a valid return code
     * @throws PolicyCheckException if the policy could not be checked for any error
     */
    Optional<Boolean> check(T policy, PolicyCheckerContext context) throws PolicyCheckException;
}
