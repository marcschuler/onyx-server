package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.rules.Policy;
import de.marcschuler.webrtcserver.data.rules.SimplePolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import de.marcschuler.webrtcserver.service.policy.SimplePolicyChecker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {


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
