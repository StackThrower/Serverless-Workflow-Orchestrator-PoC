package com.workflow.engine.model;

import java.util.List;
import java.util.Map;

public record WorkflowDefinitionModel(
    String name,
    String version,
    List<StepDefinition> steps,
    Map<String, Object> variables
) {
    public StepDefinition findStep(String stepId) {
        return steps.stream()
            .filter(step -> step.id().equals(stepId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Step not found: " + stepId));
    }

    public StepDefinition getFirstStep() {
        return steps.isEmpty() ? null : steps.get(0);
    }
}
