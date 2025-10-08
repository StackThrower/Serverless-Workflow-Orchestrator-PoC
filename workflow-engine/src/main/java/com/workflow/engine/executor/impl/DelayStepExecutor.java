package com.workflow.engine.executor.impl;

import com.workflow.engine.executor.StepExecutor;
import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class DelayStepExecutor implements StepExecutor {

    @Override
    public String getStepType() {
        return "delay";
    }

    @Override
    public Mono<Object> execute(StepDefinition stepDefinition, ExecutionContext context) {
        String durationStr = stepDefinition.getConfigString("duration");
        Duration duration = parseDuration(durationStr);

        return Mono.delay(duration)
            .map(tick -> Map.of(
                "delayed", duration.toString(),
                "delayedMs", duration.toMillis(),
                "timestamp", System.currentTimeMillis()
            ));
    }

    private Duration parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return Duration.ofSeconds(1);
        }

        try {
            // Support formats like "5s", "10m", "1h"
            if (durationStr.endsWith("s")) {
                return Duration.ofSeconds(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            } else if (durationStr.endsWith("m")) {
                return Duration.ofMinutes(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            } else if (durationStr.endsWith("h")) {
                return Duration.ofHours(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            } else {
                // Default to seconds if no unit specified
                return Duration.ofSeconds(Long.parseLong(durationStr));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration format: " + durationStr, e);
        }
    }
}
