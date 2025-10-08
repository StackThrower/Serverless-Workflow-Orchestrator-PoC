package com.workflow.storage.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("workflow_definitions")
public record WorkflowDefinition(
    @Id
    UUID id,
    String name,
    String yamlContent,
    String version,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean active
) {
    public static WorkflowDefinition create(String name, String yamlContent, String version) {
        var now = LocalDateTime.now();
        return new WorkflowDefinition(
            UUID.randomUUID(),
            name,
            yamlContent,
            version,
            now,
            now,
            true
        );
    }
}
