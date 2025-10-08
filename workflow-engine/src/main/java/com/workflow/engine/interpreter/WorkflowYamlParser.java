package com.workflow.engine.interpreter;

import com.workflow.engine.model.StepDefinition;
import com.workflow.engine.model.WorkflowDefinitionModel;
import org.yaml.snakeyaml.Yaml;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WorkflowYamlParser {

    private final Yaml yaml = new Yaml();

    public WorkflowDefinitionModel parseWorkflow(String yamlContent) {
        try {
            Map<String, Object> workflowData = yaml.load(yamlContent);

            String name = (String) workflowData.get("name");
            String version = (String) workflowData.getOrDefault("version", "1.0.0");
            Map<String, Object> variables = (Map<String, Object>) workflowData.getOrDefault("variables", Map.of());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> stepsData = (List<Map<String, Object>>) workflowData.get("steps");

            List<StepDefinition> steps = stepsData.stream()
                .map(this::parseStep)
                .collect(Collectors.toList());

            return new WorkflowDefinitionModel(name, version, steps, variables);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid workflow YAML: " + e.getMessage(), e);
        }
    }

    private StepDefinition parseStep(Map<String, Object> stepData) {
        String id = (String) stepData.get("id");
        String type = (String) stepData.get("type");
        String next = (String) stepData.get("next");
        String onTrue = (String) stepData.get("onTrue");
        String onFalse = (String) stepData.get("onFalse");

        // Extract all config parameters except control flow
        Map<String, Object> config = stepData.entrySet().stream()
            .filter(entry -> !List.of("id", "type", "next", "onTrue", "onFalse").contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new StepDefinition(id, type, config, next, onTrue, onFalse);
    }
}
