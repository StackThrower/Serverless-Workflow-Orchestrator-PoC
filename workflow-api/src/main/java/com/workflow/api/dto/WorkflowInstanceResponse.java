package com.workflow.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowInstanceResponse(
    UUID id,
    UUID workflowDefinitionId,
    String status,
    String currentStepId,
    String input,
    String output,
    String errorMessage,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    LocalDateTime updatedAt
) {
}
