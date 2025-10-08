package com.workflow.engine.executor.impl;

import com.workflow.engine.executor.StepExecutor;
import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ConditionStepExecutor implements StepExecutor {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String getStepType() {
        return "condition";
    }

    @Override
    public Mono<Object> execute(StepDefinition stepDefinition, ExecutionContext context) {
        String expression = stepDefinition.getConfigString("expression");

        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Condition step requires an 'expression' parameter");
        }

        try {
            // Create evaluation context with workflow variables
            StandardEvaluationContext evalContext = new StandardEvaluationContext();
            context.getVariables().forEach(evalContext::setVariable);

            // Parse and evaluate the SpEL expression
            Expression exp = parser.parseExpression(expression);
            Boolean result = exp.getValue(evalContext, Boolean.class);

            // Store the condition result in the execution context
            context.setConditionResult(result != null && result);

            return Mono.just(Map.of(
                "expression", expression,
                "result", result != null && result,
                "evaluatedAt", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to evaluate condition expression: " + expression, e);
        }
    }
}
