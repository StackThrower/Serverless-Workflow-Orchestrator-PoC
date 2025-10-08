package com.workflow.engine.model;

import java.util.Map;

public record StepDefinition(
    String id,
    String type,
    Map<String, Object> config,
    String next,
    String onTrue,
    String onFalse
) {
    public String getNextStep(boolean conditionResult) {
        return switch (type) {
            case "condition" -> conditionResult ? onTrue : onFalse;
            default -> next;
        };
    }

    public <T> T getConfigValue(String key, Class<T> type) {
        Object value = config.get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    public String getConfigString(String key) {
        return getConfigValue(key, String.class);
    }

    public Integer getConfigInt(String key) {
        return getConfigValue(key, Integer.class);
    }

    public Boolean getConfigBoolean(String key) {
        return getConfigValue(key, Boolean.class);
    }
}
