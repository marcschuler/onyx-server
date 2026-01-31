package de.marcschuler.webrtcserver.service.policy;

import de.marcschuler.webrtcserver.data.policy.SpeLPolicy;
import de.marcschuler.webrtcserver.error.webclient.PolicyCheckException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

//TODO check - it's experimenal right now
@Service
@RequiredArgsConstructor
@Slf4j
public class SpelPolicyChecker implements PolicyChecker<SpeLPolicy> {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Optional<Boolean> check(SpeLPolicy policy, Map<String, Object> context) throws PolicyCheckException {
        var exp = parser.parseExpression(policy.getSpel());
        var result = exp.getValue(context, Boolean.class);
        return Optional.ofNullable(result);
    }
}
