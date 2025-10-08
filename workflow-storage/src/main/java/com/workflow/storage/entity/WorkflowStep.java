package com.workflow.storage.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("workflow_steps")
public record WorkflowStep(
    @Id
    UUID id,
    UUID workflowInstanceId,
    String stepId,
    String stepType,
    String stepConfig,
    String status, // PENDING, RUNNING, COMPLETED, FAILED, SKIPPED
    String input,
    String output,
    String errorMessage,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    int retryCount,
    int maxRetries
) {
    public static WorkflowStep create(UUID workflowInstanceId, String stepId, String stepType, String stepConfig, String input) {
        return new WorkflowStep(
            UUID.randomUUID(),
            workflowInstanceId,
            stepId,
            stepType,
            stepConfig,
            "PENDING",
            input,
            null,
            null,
            null,
            null,
            0,
            3
        );
    }

    public WorkflowStep withStatus(String newStatus) {
        return new WorkflowStep(
            id, workflowInstanceId, stepId, stepType, stepConfig, newStatus, input, output, errorMessage,
            newStatus.equals("RUNNING") ? LocalDateTime.now() : startedAt,
            (newStatus.equals("COMPLETED") || newStatus.equals("FAILED")) ? LocalDateTime.now() : completedAt,
            retryCount, maxRetries
        );
    }

    public WorkflowStep withResult(String result) {
        return new WorkflowStep(
            id, workflowInstanceId, stepId, stepType, stepConfig, "COMPLETED", input, result, errorMessage,
            startedAt, LocalDateTime.now(), retryCount, maxRetries
        );
    }

    public WorkflowStep withError(String error) {
        return new WorkflowStep(
            id, workflowInstanceId, stepId, stepType, stepConfig, "FAILED", input, output, error,
            startedAt, LocalDateTime.now(), retryCount, maxRetries
        );
    }

    public WorkflowStep withRetry() {
        return new WorkflowStep(
            id, workflowInstanceId, stepId, stepType, stepConfig, "PENDING", input, null, null,
            null, null, retryCount + 1, maxRetries
        );
    }
}
