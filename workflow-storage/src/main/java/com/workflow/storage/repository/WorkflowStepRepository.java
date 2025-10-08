package com.workflow.storage.repository;

import com.workflow.storage.entity.WorkflowStep;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface WorkflowStepRepository extends R2dbcRepository<WorkflowStep, UUID> {

    Flux<WorkflowStep> findByWorkflowInstanceIdOrderByStartedAt(UUID workflowInstanceId);

    Flux<WorkflowStep> findByStatus(String status);

    Mono<WorkflowStep> findByWorkflowInstanceIdAndStepId(UUID workflowInstanceId, String stepId);

    Flux<WorkflowStep> findByWorkflowInstanceIdAndStatus(UUID workflowInstanceId, String status);
}
