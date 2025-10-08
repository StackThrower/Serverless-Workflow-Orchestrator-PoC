package com.workflow.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowUpdateHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowUpdateHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Static sink for broadcasting updates to all connected clients
    private static final Sinks.Many<String> updateSink = Sinks.many().multicast().onBackpressureBuffer();
    private static final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        activeSessions.put(sessionId, session);

        logger.info("WebSocket session connected: {}", sessionId);

        // Send periodic heartbeat and workflow updates
        Flux<WebSocketMessage> output = Flux.merge(
            // Heartbeat every 30 seconds
            Flux.interval(Duration.ofSeconds(30))
                .map(tick -> createMessage(session, Map.of("type", "heartbeat", "timestamp", System.currentTimeMillis()))),

            // Workflow updates
            updateSink.asFlux()
                .map(update -> session.textMessage(update))
        );

        return session.send(output)
            .doOnTerminate(() -> {
                activeSessions.remove(sessionId);
                logger.info("WebSocket session disconnected: {}", sessionId);
            });
    }

    public static void broadcastUpdate(String type, Object data) {
        try {
            Map<String, Object> message = Map.of(
                "type", type,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            String jsonMessage = objectMapper.writeValueAsString(message);
            updateSink.tryEmitNext(jsonMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing workflow update", e);
        }
    }

    private WebSocketMessage createMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            return session.textMessage(json);
        } catch (JsonProcessingException e) {
            logger.error("Error creating WebSocket message", e);
            return session.textMessage("{\"type\":\"error\",\"message\":\"Serialization error\"}");
        }
    }
}
