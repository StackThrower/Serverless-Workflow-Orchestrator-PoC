package com.workflow.engine.registry;

import com.workflow.engine.executor.StepExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StepExecutorRegistry {

    private final Map<String, StepExecutor> executors;

    public StepExecutorRegistry(List<StepExecutor> stepExecutors) {
        this.executors = stepExecutors.stream()
            .collect(Collectors.toMap(StepExecutor::getStepType, Function.identity()));
    }

    public StepExecutor getExecutor(String stepType) {
        StepExecutor executor = executors.get(stepType);
        if (executor == null) {
            throw new IllegalArgumentException("No executor found for step type: " + stepType);
        }
        return executor;
    }

    public boolean hasExecutor(String stepType) {
        return executors.containsKey(stepType);
    }

    public List<String> getSupportedStepTypes() {
        return List.copyOf(executors.keySet());
    }
}
