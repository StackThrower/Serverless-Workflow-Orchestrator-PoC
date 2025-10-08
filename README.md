# Serverless Workflow Orchestrator

A lightweight analog of Temporal.io / AWS Step Functions built with Spring Boot that executes YAML-described workflow processes and manages their state in PostgreSQL.

## 🎯 Project Overview

This project demonstrates expertise in asynchronous flows, DSL interpretation, and state management by providing a reactive workflow orchestration engine that can execute complex multi-step processes defined in YAML format.

## 🏗️ Architecture

The project is structured as a multi-module Maven application:

- **workflow-storage** - Data access layer with PostgreSQL entities and repositories
- **workflow-engine** - Core workflow engine with YAML parsing and step execution
- **workflow-api** - REST API with WebSocket support for real-time monitoring

## 🚀 Features

- ✅ YAML-based workflow definition
- ✅ Reactive step execution with backpressure handling
- ✅ State persistence and recovery
- ✅ Real-time monitoring via WebSocket
- ✅ Extensible step executor framework
- ✅ Built-in step types: HTTP, Delay, Condition, Log
- ✅ Expression evaluation with Spring SpEL
- ✅ Comprehensive error handling and retry logic

## 🛠️ Technology Stack

- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3** - Reactive web framework
- **Spring WebFlux** - Non-blocking I/O
- **Project Reactor** - Reactive programming
- **PostgreSQL + R2DBC** - Reactive database access
- **SnakeYAML** - YAML parsing
- **WebSocket** - Real-time updates
- **Docker Compose** - Local development environment

## 📋 Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### 1. Clone and Build
```bash
git clone <repository-url>
cd Serverless-Workflow-Orchestrator-PoC
mvn clean install
```

### 2. Start Infrastructure
```bash
docker-compose up -d postgres
```

### 3. Run Application
```bash
cd workflow-api
mvn spring-boot:run
```

### 4. Test Workflow Execution
```bash
# Start a workflow
curl -X POST http://localhost:8080/api/workflows/start/sample-workflow \
  -H "Content-Type: application/json" \
  -d '{"input": "{\"userId\": 123}"}'

# Check workflow status
curl http://localhost:8080/api/workflows/{workflow-instance-id}

# View workflow steps
curl http://localhost:8080/api/workflows/{workflow-instance-id}/steps
```

## 📄 Workflow Definition Format

Workflows are defined in YAML format with the following structure:

```yaml
name: example-workflow
version: 1.0.0
variables:
  timeout: 30s
  maxRetries: 3

steps:
  - id: fetch-data
    type: http
    method: GET
    url: https://api.example.com/data
    next: validate-response

  - id: validate-response
    type: condition
    expression: "#result['status'] == 200"
    onTrue: process-data
    onFalse: handle-error

  - id: process-data
    type: delay
    duration: 2s
    next: save-result

  - id: save-result
    type: http
    method: POST
    url: https://api.example.com/save
    body:
      data: "#result"
      timestamp: "${System.currentTimeMillis()}"

  - id: handle-error
    type: log
    message: "API call failed: ${result}"
    level: error
```

## 🔧 Supported Step Types

### HTTP Step
```yaml
- id: api-call
  type: http
  method: GET|POST|PUT|DELETE
  url: https://example.com/api
  body: { "key": "value" }  # For POST/PUT requests
```

### Delay Step
```yaml
- id: wait
  type: delay
  duration: 5s  # Supports s, m, h units
```

### Condition Step
```yaml
- id: check-result
  type: condition
  expression: "#result['status'] == 200"
  onTrue: success-step
  onFalse: error-step
```

### Log Step
```yaml
- id: log-info
  type: log
  message: "Processing complete: ${result}"
  level: info  # debug, info, warn, error
```

## 🌐 API Endpoints

### Workflow Management
- `POST /api/workflows/start/{workflowName}` - Start workflow execution
- `GET /api/workflows/{instanceId}` - Get workflow instance status
- `GET /api/workflows` - List all workflow instances
- `GET /api/workflows/{instanceId}/steps` - Get workflow step history

### Workflow Definitions
- `POST /api/definitions` - Create workflow definition
- `GET /api/definitions` - List all workflow definitions

### Real-time Updates
- `WebSocket /ws/workflow-updates` - Real-time workflow status updates

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing with Sample Workflows
The application includes pre-loaded sample workflows:
- `sample-workflow` - Basic HTTP → Delay → Condition flow
- `data-processing-workflow` - Data fetching and processing example

## 🐳 Docker Deployment

### Build and Run with Docker Compose
```bash
docker-compose up --build
```

This will start:
- PostgreSQL database (port 5432)
- Workflow API (port 8080)

## 🔍 Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### WebSocket Connection (JavaScript)
```javascript
const ws = new WebSocket('ws://localhost:8080/ws/workflow-updates');
ws.onmessage = (event) => {
  const update = JSON.parse(event.data);
  console.log('Workflow update:', update);
};
```

## 🏗️ Extending the Framework

### Adding Custom Step Types

1. Implement `StepExecutor` interface:
```java
@Component
public class CustomStepExecutor implements StepExecutor {
    @Override
    public String getStepType() {
        return "custom";
    }
    
    @Override
    public Mono<Object> execute(StepDefinition step, ExecutionContext context) {
        // Custom logic here
        return Mono.just(result);
    }
}
```

2. The executor will be automatically registered via Spring's component scanning.

## 📊 Performance Characteristics

- **Reactive Architecture**: Non-blocking I/O throughout the stack
- **Backpressure Handling**: Built-in flow control with Project Reactor
- **State Persistence**: Workflow state saved after each step
- **Fault Tolerance**: Automatic retry logic and error handling
- **Scalability**: Horizontally scalable with proper database configuration

## 🔐 Security Considerations

- Input validation on all API endpoints
- SQL injection protection via R2DBC parameterized queries
- Expression evaluation sandboxing with Spring SpEL
- WebSocket connection management and rate limiting

## 📚 Further Development

Potential enhancements:
- [ ] Workflow versioning and migration
- [ ] Parallel step execution
- [ ] Workflow scheduling (cron-like)
- [ ] Metrics and observability integration
- [ ] GraphQL API
- [ ] Web UI for workflow visualization
- [ ] Workflow templates and libraries

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.
