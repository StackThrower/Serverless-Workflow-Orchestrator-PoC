package com.workflow.engine.interpreter;

import com.workflow.engine.model.StepDefinition;
import com.workflow.engine.model.WorkflowDefinitionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowYamlParserTest {

    private WorkflowYamlParser yamlParser;

    @BeforeEach
    void setUp() {
        yamlParser = new WorkflowYamlParser();
    }

    @Test
    void shouldParseValidWorkflowYaml() {
        // Given
        String yaml = """
            name: test-workflow
            version: 1.0.0
            variables:
              timeout: 30s
            steps:
              - id: step1
                type: http
                method: GET
                url: https://example.com
                next: step2
              - id: step2
                type: log
                message: "Done"
            """;

        // When
        WorkflowDefinitionModel workflow = yamlParser.parseWorkflow(yaml);

        // Then
        assertThat(workflow.name()).isEqualTo("test-workflow");
        assertThat(workflow.version()).isEqualTo("1.0.0");
        assertThat(workflow.steps()).hasSize(2);
        assertThat(workflow.variables()).containsEntry("timeout", "30s");

        StepDefinition step1 = workflow.steps().get(0);
        assertThat(step1.id()).isEqualTo("step1");
        assertThat(step1.type()).isEqualTo("http");
        assertThat(step1.getConfigString("method")).isEqualTo("GET");
        assertThat(step1.getConfigString("url")).isEqualTo("https://example.com");
        assertThat(step1.next()).isEqualTo("step2");
    }

    @Test
    void shouldThrowExceptionForInvalidYaml() {
        // Given
        String invalidYaml = "invalid: yaml: content: [";

        // When/Then
        assertThatThrownBy(() -> yamlParser.parseWorkflow(invalidYaml))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid workflow YAML");
    }

    @Test
    void shouldParseConditionalSteps() {
        // Given
        String yaml = """
            name: conditional-workflow
            steps:
              - id: condition-step
                type: condition
                expression: "#{result.status == 200}"
                onTrue: success-step
                onFalse: error-step
            """;

        // When
        WorkflowDefinitionModel workflow = yamlParser.parseWorkflow(yaml);

        // Then
        StepDefinition conditionStep = workflow.steps().get(0);
        assertThat(conditionStep.type()).isEqualTo("condition");
        assertThat(conditionStep.onTrue()).isEqualTo("success-step");
        assertThat(conditionStep.onFalse()).isEqualTo("error-step");
    }
}
