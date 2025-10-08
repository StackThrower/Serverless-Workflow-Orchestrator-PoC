package com.workflow.storage.repository;

import com.workflow.storage.entity.WorkflowDefinition;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface WorkflowDefinitionRepository extends R2dbcRepository<WorkflowDefinition, UUID> {

    Mono<WorkflowDefinition> findByNameAndActive(String name, boolean active);

    Mono<WorkflowDefinition> findByNameAndVersion(String name, String version);
}
