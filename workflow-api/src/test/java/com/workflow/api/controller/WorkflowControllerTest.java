package com.workflow.api.controller;

import com.workflow.api.dto.StartWorkflowRequest;
import com.workflow.api.dto.WorkflowInstanceResponse;
import com.workflow.api.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowControllerTest {

    @Mock
    private WorkflowService workflowService;

    private WorkflowController workflowController;

    @BeforeEach
    void setUp() {
        workflowController = new WorkflowController(workflowService);
    }

    @Test
    void shouldStartWorkflow() {
        // Given
        UUID instanceId = UUID.randomUUID();
        UUID definitionId = UUID.randomUUID();
        WorkflowInstanceResponse response = new WorkflowInstanceResponse(
            instanceId,
            definitionId,
            "RUNNING",
            "step1",
            "{\"test\": \"data\"}",
            null,
            null,
            LocalDateTime.now(),
            null,
            LocalDateTime.now()
        );

        when(workflowService.startWorkflow(eq("test-workflow"), any()))
            .thenReturn(Mono.just(response));

        StartWorkflowRequest request = new StartWorkflowRequest("{\"test\": \"data\"}");

        // When
        Mono<WorkflowInstanceResponse> result = workflowController.startWorkflow("test-workflow", request);

        // Then
        StepVerifier.create(result)
            .expectNext(response)
            .verifyComplete();
    }

    @Test
    void shouldGetWorkflowInstance() {
        // Given
        UUID instanceId = UUID.randomUUID();
        UUID definitionId = UUID.randomUUID();
        WorkflowInstanceResponse response = new WorkflowInstanceResponse(
            instanceId,
            definitionId,
            "COMPLETED",
            null,
            "{\"test\": \"data\"}",
            "{\"result\": \"success\"}",
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(workflowService.getWorkflowInstance(instanceId))
            .thenReturn(Mono.just(response));

        // When
        Mono<WorkflowInstanceResponse> result = workflowController.getWorkflowInstance(instanceId);

        // Then
        StepVerifier.create(result)
            .expectNext(response)
            .verifyComplete();
    }

    @Test
    void shouldGetAllWorkflowInstances() {
        // Given
        UUID instanceId = UUID.randomUUID();
        UUID definitionId = UUID.randomUUID();
        WorkflowInstanceResponse response = new WorkflowInstanceResponse(
            instanceId,
            definitionId,
            "COMPLETED",
            null,
            "{\"test\": \"data\"}",
            "{\"result\": \"success\"}",
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(workflowService.getAllWorkflowInstances())
            .thenReturn(Flux.just(response));

        // When
        Flux<WorkflowInstanceResponse> result = workflowController.getAllWorkflowInstances();

        // Then
        StepVerifier.create(result)
            .expectNext(response)
            .verifyComplete();
    }

    @Test
    void shouldReturnCorrectControllerInstance() {
        // Given/When/Then
        assertThat(workflowController).isNotNull();
        assertThat(workflowController).isInstanceOf(WorkflowController.class);
    }
}
