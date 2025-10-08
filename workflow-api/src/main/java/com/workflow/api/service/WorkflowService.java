package com.workflow.api.service;

import com.workflow.api.dto.WorkflowInstanceResponse;
import com.workflow.api.dto.WorkflowStepResponse;
import com.workflow.engine.executor.WorkflowExecutor;
import com.workflow.storage.entity.WorkflowDefinition;
import com.workflow.storage.entity.WorkflowInstance;
import com.workflow.storage.entity.WorkflowStep;
import com.workflow.storage.repository.WorkflowDefinitionRepository;
import com.workflow.storage.repository.WorkflowInstanceRepository;
import com.workflow.storage.repository.WorkflowStepRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class WorkflowService {

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowExecutor workflowExecutor;

    public WorkflowService(WorkflowDefinitionRepository definitionRepository,
                          WorkflowInstanceRepository instanceRepository,
                          WorkflowStepRepository stepRepository,
                          WorkflowExecutor workflowExecutor) {
        this.definitionRepository = definitionRepository;
        this.instanceRepository = instanceRepository;
        this.stepRepository = stepRepository;
        this.workflowExecutor = workflowExecutor;
    }

    public Mono<WorkflowInstanceResponse> startWorkflow(String workflowName, String input) {
        return definitionRepository.findByNameAndActive(workflowName, true)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Workflow not found: " + workflowName)))
            .flatMap(definition -> workflowExecutor.executeWorkflow(definition, input))
            .map(this::mapToResponse);
    }

    public Mono<WorkflowInstanceResponse> getWorkflowInstance(UUID instanceId) {
        return instanceRepository.findById(instanceId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Workflow instance not found: " + instanceId)))
            .map(this::mapToResponse);
    }

    public Flux<WorkflowInstanceResponse> getAllWorkflowInstances() {
        return instanceRepository.findAll()
            .map(this::mapToResponse);
    }

    public Flux<WorkflowStepResponse> getWorkflowSteps(UUID instanceId) {
        return stepRepository.findByWorkflowInstanceIdOrderByStartedAt(instanceId)
            .map(this::mapStepToResponse);
    }

    public Mono<WorkflowDefinition> createWorkflowDefinition(String name, String yamlContent, String version) {
        WorkflowDefinition definition = WorkflowDefinition.create(name, yamlContent, version);
        return definitionRepository.save(definition);
    }

    public Flux<WorkflowDefinition> getAllWorkflowDefinitions() {
        return definitionRepository.findAll();
    }

    private WorkflowInstanceResponse mapToResponse(WorkflowInstance instance) {
        return new WorkflowInstanceResponse(
            instance.id(),
            instance.workflowDefinitionId(),
            instance.status(),
            instance.currentStepId(),
            instance.input(),
            instance.output(),
            instance.errorMessage(),
            instance.startedAt(),
            instance.completedAt(),
            instance.updatedAt()
        );
    }

    private WorkflowStepResponse mapStepToResponse(WorkflowStep step) {
        return new WorkflowStepResponse(
            step.id(),
            step.workflowInstanceId(),
            step.stepId(),
            step.stepType(),
            step.status(),
            step.input(),
            step.output(),
            step.errorMessage(),
            step.startedAt(),
            step.completedAt(),
            step.retryCount()
        );
    }
}
