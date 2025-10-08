package com.workflow.api.controller;

import com.workflow.api.service.WorkflowService;
import com.workflow.storage.entity.WorkflowDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/definitions")
@CrossOrigin(origins = "*")
public class WorkflowDefinitionController {

    private final WorkflowService workflowService;

    public WorkflowDefinitionController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkflowDefinition> createWorkflowDefinition(
            @RequestParam String name,
            @RequestParam String version,
            @RequestBody String yamlContent) {
        return workflowService.createWorkflowDefinition(name, yamlContent, version);
    }

    @GetMapping
    public Flux<WorkflowDefinition> getAllWorkflowDefinitions() {
        return workflowService.getAllWorkflowDefinitions();
    }
}
