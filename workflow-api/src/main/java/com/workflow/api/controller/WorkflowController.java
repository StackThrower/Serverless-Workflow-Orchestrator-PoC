package com.workflow.api.controller;

import com.workflow.api.dto.StartWorkflowRequest;
import com.workflow.api.dto.WorkflowInstanceResponse;
import com.workflow.api.dto.WorkflowStepResponse;
import com.workflow.api.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/start/{workflowName}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<WorkflowInstanceResponse> startWorkflow(
            @PathVariable String workflowName,
            @RequestBody StartWorkflowRequest request) {
        return workflowService.startWorkflow(workflowName, request.input());
    }

    @GetMapping("/{instanceId}")
    public Mono<WorkflowInstanceResponse> getWorkflowInstance(@PathVariable UUID instanceId) {
        return workflowService.getWorkflowInstance(instanceId);
    }

    @GetMapping
    public Flux<WorkflowInstanceResponse> getAllWorkflowInstances() {
        return workflowService.getAllWorkflowInstances();
    }

    @GetMapping("/{instanceId}/steps")
    public Flux<WorkflowStepResponse> getWorkflowSteps(@PathVariable UUID instanceId) {
        return workflowService.getWorkflowSteps(instanceId);
    }
}
