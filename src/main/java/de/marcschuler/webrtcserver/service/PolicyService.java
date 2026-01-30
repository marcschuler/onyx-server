package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.rules.Policy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

    private ExpressionParser parser;

    @PostConstruct
    void init() {
        this.parser = new SpelExpressionParser();
    }

    public EvaluationContext buildContext(Map<String, Object> map) {
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding()
                .withInstanceMethods()
                .build();
        map.forEach(context::setVariable);
        return context;
    }


    public PolicyResult canAccessPerRule(List<Policy> policies, EvaluationContext context, PolicyResult defaultValue) {
        var sortedPolicies = policies.stream().sorted().toList();
        for (var policy : sortedPolicies) {
            var result = canAccessPerRule(policy, context);
            if (result != null)
                return result;
        }
        return defaultValue;
    }


    public PolicyResult canAccessPerRule(Policy policy, EvaluationContext context) {
        var result = parser.parseExpression(policy.getSpel()).getValue(context, Boolean.class);
        if (result != null) {
            return result ? PolicyResult.ALLOW : PolicyResult.DENY;
        }
        return null;
    }

    public enum PolicyResult {
        ALLOW,
        DENY
    }
}
