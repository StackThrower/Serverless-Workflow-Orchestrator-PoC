package com.workflow.engine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecutionContext {
    private final UUID workflowInstanceId;
    private final Map<String, Object> variables;
    private String currentStepId;
    private Object lastResult;
    private boolean conditionResult;

    public ExecutionContext(UUID workflowInstanceId, Map<String, Object> initialVariables) {
        this.workflowInstanceId = workflowInstanceId;
        this.variables = new HashMap<>(initialVariables);
    }

    public UUID getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public Object getLastResult() {
        return lastResult;
    }

    public void setLastResult(Object lastResult) {
        this.lastResult = lastResult;
        // Store result in variables for expression evaluation
        variables.put("result", lastResult);
    }

    public boolean isConditionResult() {
        return conditionResult;
    }

    public void setConditionResult(boolean conditionResult) {
        this.conditionResult = conditionResult;
    }

    public ExecutionContext copy() {
        ExecutionContext copy = new ExecutionContext(workflowInstanceId, new HashMap<>(variables));
        copy.currentStepId = this.currentStepId;
        copy.lastResult = this.lastResult;
        copy.conditionResult = this.conditionResult;
        return copy;
    }
}
