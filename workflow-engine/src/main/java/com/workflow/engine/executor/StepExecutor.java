package com.workflow.engine.executor;

import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import reactor.core.publisher.Mono;

public interface StepExecutor {

    String getStepType();

    Mono<Object> execute(StepDefinition stepDefinition, ExecutionContext context);

    default boolean canHandle(String stepType) {
        return getStepType().equals(stepType);
    }
}
