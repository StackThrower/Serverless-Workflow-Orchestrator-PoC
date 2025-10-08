package com.workflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.workflow.api",
    "com.workflow.engine",
    "com.workflow.storage"
})
public class WorkflowOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowOrchestratorApplication.class, args);
    }
}
