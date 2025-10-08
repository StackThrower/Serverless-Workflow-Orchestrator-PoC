-- Database schema for Workflow Orchestrator
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Workflow definitions table
CREATE TABLE workflow_definitions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    yaml_content TEXT NOT NULL,
    version VARCHAR(50) NOT NULL DEFAULT '1.0.0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT true,
    UNIQUE(name, version)
);

-- Workflow instances table
CREATE TABLE workflow_instances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workflow_definition_id UUID NOT NULL REFERENCES workflow_definitions(id),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    current_step_id VARCHAR(255),
    input TEXT,
    output TEXT,
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Workflow steps table
CREATE TABLE workflow_steps (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workflow_instance_id UUID NOT NULL REFERENCES workflow_instances(id),
    step_id VARCHAR(255) NOT NULL,
    step_type VARCHAR(50) NOT NULL,
    step_config TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    input TEXT,
    output TEXT,
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3
);

-- Indexes for better performance
CREATE INDEX idx_workflow_definitions_name_active ON workflow_definitions(name, active);
CREATE INDEX idx_workflow_instances_status ON workflow_instances(status);
CREATE INDEX idx_workflow_instances_definition_id ON workflow_instances(workflow_definition_id);
CREATE INDEX idx_workflow_steps_instance_id ON workflow_steps(workflow_instance_id);
CREATE INDEX idx_workflow_steps_status ON workflow_steps(status);
CREATE INDEX idx_workflow_instances_updated_at ON workflow_instances(updated_at);
