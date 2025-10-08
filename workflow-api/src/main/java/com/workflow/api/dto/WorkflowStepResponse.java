package com.workflow.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowStepResponse(
    UUID id,
    UUID workflowInstanceId,
    String stepId,
    String stepType,
    String status,
    String input,
    String output,
    String errorMessage,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    int retryCount
) {
}
