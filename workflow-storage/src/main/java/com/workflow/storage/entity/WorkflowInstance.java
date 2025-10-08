package com.workflow.storage.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("workflow_instances")
public record WorkflowInstance(
    @Id
    UUID id,
    UUID workflowDefinitionId,
    String status, // PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    String currentStepId,
    String input,
    String output,
    String errorMessage,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    LocalDateTime updatedAt
) {
    public static WorkflowInstance create(UUID workflowDefinitionId, String input) {
        var now = LocalDateTime.now();
        return new WorkflowInstance(
            UUID.randomUUID(),
            workflowDefinitionId,
            "PENDING",
            null,
            input,
            null,
            null,
            now,
            null,
            now
        );
    }

    public WorkflowInstance withStatus(String newStatus) {
        return new WorkflowInstance(
            id, workflowDefinitionId, newStatus, currentStepId, input, output, errorMessage,
            startedAt, newStatus.equals("COMPLETED") || newStatus.equals("FAILED") ? LocalDateTime.now() : completedAt,
            LocalDateTime.now()
        );
    }

    public WorkflowInstance withCurrentStep(String stepId) {
        return new WorkflowInstance(
            id, workflowDefinitionId, status, stepId, input, output, errorMessage,
            startedAt, completedAt, LocalDateTime.now()
        );
    }

    public WorkflowInstance withOutput(String newOutput) {
        return new WorkflowInstance(
            id, workflowDefinitionId, status, currentStepId, input, newOutput, errorMessage,
            startedAt, completedAt, LocalDateTime.now()
        );
    }

    public WorkflowInstance withError(String error) {
        return new WorkflowInstance(
            id, workflowDefinitionId, "FAILED", currentStepId, input, output, error,
            startedAt, LocalDateTime.now(), LocalDateTime.now()
        );
    }
}
