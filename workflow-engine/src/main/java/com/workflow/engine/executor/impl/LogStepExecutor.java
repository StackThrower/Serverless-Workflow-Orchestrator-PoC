package com.workflow.engine.executor.impl;

import com.workflow.engine.executor.StepExecutor;
import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class LogStepExecutor implements StepExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LogStepExecutor.class);

    @Override
    public String getStepType() {
        return "log";
    }

    @Override
    public Mono<Object> execute(StepDefinition stepDefinition, ExecutionContext context) {
        String message = stepDefinition.getConfigString("message");
        String level = stepDefinition.getConfigString("level");

        if (message == null) {
            message = "Log step executed";
        }

        if (level == null) {
            level = "info";
        }

        // Replace variables in message
        String processedMessage = replaceVariables(message, context);

        // Log at appropriate level
        switch (level.toLowerCase()) {
            case "debug" -> logger.debug("Workflow {}: {}", context.getWorkflowInstanceId(), processedMessage);
            case "info" -> logger.info("Workflow {}: {}", context.getWorkflowInstanceId(), processedMessage);
            case "warn" -> logger.warn("Workflow {}: {}", context.getWorkflowInstanceId(), processedMessage);
            case "error" -> logger.error("Workflow {}: {}", context.getWorkflowInstanceId(), processedMessage);
            default -> logger.info("Workflow {}: {}", context.getWorkflowInstanceId(), processedMessage);
        }

        return Mono.just(Map.of(
            "message", processedMessage,
            "level", level,
            "timestamp", System.currentTimeMillis(),
            "workflowInstanceId", context.getWorkflowInstanceId().toString()
        ));
    }

    private String replaceVariables(String message, ExecutionContext context) {
        String result = message;
        for (Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return result;
    }
}
