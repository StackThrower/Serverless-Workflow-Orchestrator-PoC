package com.workflow.engine.executor.impl;

import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DelayStepExecutorTest {

    private DelayStepExecutor delayStepExecutor;
    private ExecutionContext executionContext;

    @BeforeEach
    void setUp() {
        delayStepExecutor = new DelayStepExecutor();
        executionContext = new ExecutionContext(UUID.randomUUID(), Map.of());
    }

    @Test
    void shouldExecuteDelayWithSecondsFormat() {
        // Given
        StepDefinition stepDefinition = new StepDefinition(
            "delay-step",
            "delay",
            Map.of("duration", "2s"),
            null,
            null,
            null
        );

        // When
        Mono<Object> result = delayStepExecutor.execute(stepDefinition, executionContext);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(output -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = (Map<String, Object>) output;
                return resultMap.containsKey("delayed") &&
                       resultMap.containsKey("delayedMs") &&
                       (Long) resultMap.get("delayedMs") == 2000L;
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnCorrectStepType() {
        assertThat(delayStepExecutor.getStepType()).isEqualTo("delay");
    }
}
