package com.workflow.engine.executor;

import com.workflow.engine.interpreter.WorkflowYamlParser;
import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import com.workflow.engine.model.WorkflowDefinitionModel;
import com.workflow.engine.registry.StepExecutorRegistry;
import com.workflow.storage.entity.WorkflowDefinition;
import com.workflow.storage.entity.WorkflowInstance;
import com.workflow.storage.entity.WorkflowStep;
import com.workflow.storage.repository.WorkflowInstanceRepository;
import com.workflow.storage.repository.WorkflowStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
public class WorkflowExecutor {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutor.class);

    private final WorkflowYamlParser yamlParser;
    private final StepExecutorRegistry executorRegistry;
    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowStepRepository stepRepository;

    public WorkflowExecutor(WorkflowYamlParser yamlParser,
                           StepExecutorRegistry executorRegistry,
                           WorkflowInstanceRepository instanceRepository,
                           WorkflowStepRepository stepRepository) {
        this.yamlParser = yamlParser;
        this.executorRegistry = executorRegistry;
        this.instanceRepository = instanceRepository;
        this.stepRepository = stepRepository;
    }

    public Mono<WorkflowInstance> executeWorkflow(WorkflowDefinition definition, String input) {
        logger.info("Starting workflow execution for definition: {}", definition.name());

        return Mono.fromCallable(() -> yamlParser.parseWorkflow(definition.yamlContent()))
            .flatMap(workflowModel -> {
                // Create workflow instance
                WorkflowInstance instance = WorkflowInstance.create(definition.id(), input);
                return instanceRepository.save(instance)
                    .flatMap(savedInstance -> executeWorkflowSteps(workflowModel, savedInstance, input));
            })
            .doOnSuccess(instance -> logger.info("Workflow execution completed: {}", instance.id()))
            .doOnError(error -> logger.error("Workflow execution failed", error));
    }

    private Mono<WorkflowInstance> executeWorkflowSteps(WorkflowDefinitionModel workflowModel,
                                                       WorkflowInstance instance,
                                                       String input) {
        ExecutionContext context = new ExecutionContext(instance.id(), workflowModel.variables());

        // Parse input as JSON if possible, otherwise store as string
        try {
            context.setVariable("input", input);
        } catch (Exception e) {
            context.setVariable("input", input);
        }

        // Start with the first step
        StepDefinition firstStep = workflowModel.getFirstStep();
        if (firstStep == null) {
            return instanceRepository.save(instance.withStatus("COMPLETED"));
        }

        // Update instance status to RUNNING
        return instanceRepository.save(instance.withStatus("RUNNING"))
            .flatMap(runningInstance -> executeStepChain(workflowModel, runningInstance, firstStep, context));
    }

    private Mono<WorkflowInstance> executeStepChain(WorkflowDefinitionModel workflowModel,
                                                   WorkflowInstance instance,
                                                   StepDefinition step,
                                                   ExecutionContext context) {
        if (step == null) {
            // No more steps, complete the workflow
            return instanceRepository.save(instance.withStatus("COMPLETED"));
        }

        logger.debug("Executing step: {} of type: {}", step.id(), step.type());
        context.setCurrentStepId(step.id());

        // Create and save workflow step record
        WorkflowStep workflowStep = WorkflowStep.create(
            instance.id(),
            step.id(),
            step.type(),
            step.config().toString(),
            context.getVariable("input") != null ? context.getVariable("input").toString() : null
        );

        return stepRepository.save(workflowStep.withStatus("RUNNING"))
            .flatMap(runningStep -> executeStep(step, context)
                .flatMap(result -> {
                    // Save step result and update context
                    context.setLastResult(result);
                    return stepRepository.save(runningStep.withResult(result.toString()))
                        .then(updateInstanceCurrentStep(instance, step.id()))
                        .flatMap(updatedInstance -> {
                            // Determine next step
                            String nextStepId = step.getNextStep(context.isConditionResult());
                            if (nextStepId == null) {
                                // No next step, complete workflow
                                return instanceRepository.save(updatedInstance.withStatus("COMPLETED"));
                            } else {
                                // Continue with next step
                                StepDefinition nextStep = workflowModel.findStep(nextStepId);
                                return executeStepChain(workflowModel, updatedInstance, nextStep, context);
                            }
                        });
                })
                .onErrorResume(error -> {
                    logger.error("Step execution failed: {}", step.id(), error);
                    return stepRepository.save(runningStep.withError(error.getMessage()))
                        .then(instanceRepository.save(instance.withError(error.getMessage())));
                }));
    }

    private Mono<Object> executeStep(StepDefinition step, ExecutionContext context) {
        try {
            StepExecutor executor = executorRegistry.getExecutor(step.type());
            return executor.execute(step, context);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to execute step: " + step.id(), e));
        }
    }

    private Mono<WorkflowInstance> updateInstanceCurrentStep(WorkflowInstance instance, String stepId) {
        return instanceRepository.save(instance.withCurrentStep(stepId));
    }
}
