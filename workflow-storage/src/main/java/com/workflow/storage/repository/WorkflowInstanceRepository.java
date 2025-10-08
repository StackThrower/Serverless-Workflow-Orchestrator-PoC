package com.workflow.storage.repository;

import com.workflow.storage.entity.WorkflowInstance;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface WorkflowInstanceRepository extends R2dbcRepository<WorkflowInstance, UUID> {

    Flux<WorkflowInstance> findByStatus(String status);

    Flux<WorkflowInstance> findByWorkflowDefinitionId(UUID workflowDefinitionId);

    Mono<Long> countByStatus(String status);
}
