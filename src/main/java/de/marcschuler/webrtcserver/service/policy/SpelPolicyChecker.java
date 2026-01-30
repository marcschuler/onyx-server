package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.rules.SpeLPolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Map;
import java.util.Optional;

public class SpelPolicyChecker implements PolicyChecker<SpeLPolicy> {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Optional<Boolean> check(SpeLPolicy policy, Map<String, Object> context) throws PolicyCheckException {
        var exp = parser.parseExpression(policy.getSpel());
        var result = exp.getValue(context, Boolean.class);
        return Optional.ofNullable(result);
    }
}
