package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;

import java.util.Map;
import java.util.Optional;

public interface PolicyChecker <T extends Policy> {

    public static final String CONTEXT_USER = "user";

    Optional<Boolean> check(T policy, Map<String,Object> context) throws PolicyCheckException;
}
