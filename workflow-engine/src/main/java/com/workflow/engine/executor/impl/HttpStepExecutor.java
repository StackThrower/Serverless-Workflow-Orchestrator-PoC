package com.workflow.engine.executor.impl;

import com.workflow.engine.executor.StepExecutor;
import com.workflow.engine.model.ExecutionContext;
import com.workflow.engine.model.StepDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class HttpStepExecutor implements StepExecutor {

    private final WebClient webClient;

    public HttpStepExecutor(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getStepType() {
        return "http";
    }

    @Override
    public Mono<Object> execute(StepDefinition stepDefinition, ExecutionContext context) {
        String method = stepDefinition.getConfigString("method");
        String url = stepDefinition.getConfigString("url");
        Object body = stepDefinition.getConfigValue("body", Object.class);

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

        WebClient.RequestHeadersSpec<?> requestSpec;

        if (body != null && (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.PATCH)) {
            requestSpec = webClient.method(httpMethod).uri(url).bodyValue(body);
        } else {
            requestSpec = webClient.method(httpMethod).uri(url);
        }

        return requestSpec
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> {
                // Create a response object with status and body
                return (Object) Map.of(
                    "status", 200,
                    "body", response,
                    "url", url,
                    "method", method
                );
            })
            .onErrorResume(error -> {
                // Handle HTTP errors
                return Mono.just((Object) Map.of(
                    "status", 500,
                    "error", error.getMessage(),
                    "url", url,
                    "method", method
                ));
            });
    }
}
