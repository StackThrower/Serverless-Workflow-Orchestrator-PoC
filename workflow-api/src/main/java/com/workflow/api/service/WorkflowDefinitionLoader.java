package com.workflow.api.service;

import com.workflow.api.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class WorkflowDefinitionLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowDefinitionLoader.class);

    private final WorkflowService workflowService;
    private final ResourceLoader resourceLoader;

    public WorkflowDefinitionLoader(WorkflowService workflowService, ResourceLoader resourceLoader) {
        this.workflowService = workflowService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Loading workflow definitions from classpath...");
        loadWorkflowDefinitions().subscribe();
    }

    private Flux<Void> loadWorkflowDefinitions() {
        try {
            Resource workflowsResource = resourceLoader.getResource("classpath:workflows/");
            if (workflowsResource.exists()) {
                Path workflowsPath = Paths.get(workflowsResource.getURI());

                return Flux.fromStream(this.getYamlFiles(workflowsPath))
                    .flatMap(this::loadWorkflowDefinition)
                    .doOnNext(definition -> logger.info("Loaded workflow definition: {}", definition.name()))
                    .doOnError(error -> logger.error("Error loading workflow definitions", error))
                    .then()
                    .flux();
            } else {
                logger.warn("Workflows directory not found in classpath");
                return Flux.empty();
            }
        } catch (Exception e) {
            logger.error("Failed to load workflow definitions", e);
            return Flux.error(e);
        }
    }

    private Stream<Path> getYamlFiles(Path workflowsPath) throws IOException {
        return Files.walk(workflowsPath)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"));
    }

    private Mono<com.workflow.storage.entity.WorkflowDefinition> loadWorkflowDefinition(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile, StandardCharsets.UTF_8);
            String fileName = yamlFile.getFileName().toString();
            String workflowName = fileName.substring(0, fileName.lastIndexOf('.'));

            return workflowService.createWorkflowDefinition(workflowName, yamlContent, "1.0.0")
                .onErrorResume(error -> {
                    logger.warn("Workflow definition '{}' already exists, skipping", workflowName);
                    return Mono.empty();
                });
        } catch (IOException e) {
            logger.error("Failed to read workflow file: {}", yamlFile, e);
            return Mono.empty();
        }
    }
}
